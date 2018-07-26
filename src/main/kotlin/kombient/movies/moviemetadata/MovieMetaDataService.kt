package kombient.movies.moviemetadata

import kombient.movies.imdb.ImdbService
import kombient.movies.repository.RatingsRepository
import kombient.movies.tmdb.TmdbService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Component
class MovieMetaDataService(
    val ratingService: RatingsRepository,
    val movieMetaDataRepository: MovieMetaDataRepository,
    val imdbService: ImdbService,
    val tmdbService: TmdbService

) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MovieMetaDataService::class.java)
    }

    @Scheduled(fixedRateString = "\${moviemetadata.frequency}", initialDelayString = "\${moviemetadata.delay}")
    fun refreshMetaData() {
        val ratingImdbs = ratingService.findDistinctImdbId()
        val metadataImdbs = movieMetaDataRepository.findDistinctImdbId()

        val newImdbIds = ratingImdbs.minus(metadataImdbs)

        newImdbIds.forEach {
            val imdbMovie = imdbService.getMovieById(it)
            val tmdbMovieSearchResult = tmdbService.findMovieByImdbId(it)
            LOGGER.info("Processing: $it")
            when {
                "series" == imdbMovie.Type.toLowerCase() -> {
                    val tmdbSeries = tmdbService.getTvById(tmdbMovieSearchResult.tv_results.first().id)
                    val votes = imdbMovie.imdbVotes.replace(",", "").toIntOrNull()
                    movieMetaDataRepository.save(MovieMetaData(it, imdbMovie.Title, imdbMovie.imdbRating.toFloatOrNull(), tmdbSeries.vote_average, votes, Instant.now(), tmdbSeries.runtime))
                }
                "movie" == imdbMovie.Type.toLowerCase() -> {
                    val tmdbMovie = tmdbService.getMovieById(tmdbMovieSearchResult.movie_results.first().id)
                    val votes = imdbMovie.imdbVotes.replace(",", "").toIntOrNull()
                    movieMetaDataRepository.save(MovieMetaData(it, imdbMovie.Title, imdbMovie.imdbRating.toFloatOrNull(), tmdbMovie.vote_average, votes, Instant.now(), tmdbMovie.runtime))
                }
            }
        }
    }
}
