package kombient.movies.moviemetadata

import feign.codec.DecodeException
import kombient.movies.imdb.ImdbService
import kombient.movies.repository.RatingsRepository
import kombient.movies.tmdb.TmdbService
import kombient.slack.SlackService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */

@Entity
@Table(name = "movie_blacklist")
data class BlackListedMovie(
    @Id
    val imdbId: String,
    val count: Int
)

@Repository
interface ImdbBlackListRepository : JpaRepository<BlackListedMovie, Long> {
    fun findByImdbId(imdbId: String): BlackListedMovie?
}

@Component
class MovieMetaDataService(
    @Autowired val ratingService: RatingsRepository,
    @Autowired val movieMetaDataRepository: MovieMetaDataRepository,
    @Autowired val imdbService: ImdbService,
    @Autowired val tmdbService: TmdbService,
    @Autowired val imdbBlackListRepository: ImdbBlackListRepository,
    @Autowired val slackService: SlackService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MovieMetaDataService::class.java)
        private const val MAX_RETRIES = 3
        private const val OUTDATED_DAYS_LIMIT = 14L
    }

    @Scheduled(fixedRateString = "\${moviemetadata.frequency}", initialDelayString = "\${moviemetadata.delay}")
    fun refreshMetaData() {
        val ratingImdbs = ratingService.findDistinctImdbId()
        val metadataImdbs = movieMetaDataRepository.findDistinctImdbId()
        val newImdbIds = ratingImdbs.minus(metadataImdbs)

        saveNewImdbsPlaceholder(newImdbIds)

        val outdatedEntities = movieMetaDataRepository.findAllByLastUpdatedLessThan(LocalDate.now().minusDays(OUTDATED_DAYS_LIMIT).atStartOfDay().toInstant(ZoneOffset.UTC))

        val blackListedMovies = imdbBlackListRepository.findAll().filter { it.count > 3 }
        LOGGER.info("Fetching these entities: $outdatedEntities")
        outdatedEntities
            .filter { !blackListedMovies.map { blackListedMovie -> blackListedMovie.imdbId }.contains(it.imdbId) }
            .forEach {
                try {
                    val imdbMovie = imdbService.getMovieById(it.imdbId)
                    val tmdbMovieSearchResult = tmdbService.findMovieByImdbId(it.imdbId)
                    LOGGER.info("Processing: $it")
                    when {
                        "series" == imdbMovie.Type.toLowerCase() -> {
                            if (!tmdbMovieSearchResult.tv_results.isEmpty()) {
                                val tmdbSeries = tmdbService.getTvById(tmdbMovieSearchResult.tv_results.first().id)
                                movieMetaDataRepository.saveAndFlush(it.copy(title = imdbMovie.Title, imdbRating = imdbMovie.imdbRating.toFloatOrNull(), runTime = tmdbSeries.runtime, lastUpdated = Instant.now()))
                            } else {
                                movieMetaDataRepository.saveAndFlush(it.copy(title = imdbMovie.Title, imdbRating = imdbMovie.imdbRating.toFloatOrNull(), runTime = 0, lastUpdated = Instant.now()))
                            }
                        }
                        "movie" == imdbMovie.Type.toLowerCase() -> {
                            if (!tmdbMovieSearchResult.movie_results.isEmpty()) {
                                val tmdbMovie = tmdbService.getMovieById(tmdbMovieSearchResult.movie_results.first().id)
                                movieMetaDataRepository.saveAndFlush(it.copy(title = imdbMovie.Title, imdbRating = imdbMovie.imdbRating.toFloatOrNull(), runTime = tmdbMovie.runtime, lastUpdated = Instant.now()))
                            } else {
                                movieMetaDataRepository.saveAndFlush(it.copy(title = imdbMovie.Title, imdbRating = imdbMovie.imdbRating.toFloatOrNull(), runTime = 0, lastUpdated = Instant.now()))
                            }
                        }
                        else -> blackList(it, "Tye is not movie or series: ${imdbMovie.Type}: $imdbMovie")

                    }
                } catch (e: DecodeException) {
                    blackListAfterRetries(it, "Decoding issues: $it")
                    LOGGER.error("Error found", e)
                }
            }

    }

    private fun blackList(it: MovieMetaData, data: String) {
        imdbBlackListRepository.saveAndFlush(BlackListedMovie(it.imdbId, MAX_RETRIES + 1))
        slackService.sendMessage("#gweli", "blacklisted: ${it.imdbId}, $data")
    }

    private fun blackListAfterRetries(it: MovieMetaData, data: String) {
        val blackListedMovie = imdbBlackListRepository.findByImdbId(it.imdbId)
        val count = blackListedMovie?.count ?: 0
        imdbBlackListRepository.saveAndFlush(BlackListedMovie(it.imdbId, count + 1))
        if (count > MAX_RETRIES - 1) {
            slackService.sendMessage("#gweli", "blacklisted: ${it.imdbId}, $data")
        }
    }

    @Transactional
    fun saveNewImdbsPlaceholder(newImdbIds: Set<String>) {
        movieMetaDataRepository.saveAll(newImdbIds.map { MovieMetaData(it, "placeholder", null, null, LocalDate.of(1970, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC), 0) })
    }
}
