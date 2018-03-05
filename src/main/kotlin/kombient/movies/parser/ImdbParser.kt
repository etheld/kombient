package kombient.movies.parser

import kombient.movies.repository.Rating
import kombient.movies.repository.RatingsRepository
import kombient.movies.tmdb.TmdbService
import kombient.slack.SlackService
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
@ConfigurationProperties(prefix = "imdb")
class ImdbParserConfig {
    var channel: String = ""
    var userconfig: Map<String, String> = HashMap()
    override fun toString(): String {
        return "ImdbParserConfig(channel='$channel', userconfig=$userconfig)"
    }
}

data class ImdbParseMovieVote(
        val imdbId: String,
        val vote: String,
        val date: LocalDate,
        val username: String)

@Component
@Transactional
class ImdbParser(
        @Autowired private val imdbParserConfig: ImdbParserConfig,
        @Autowired private val ratingsRepository: RatingsRepository,
        @Autowired private val tmdbService: TmdbService,
        @Autowired private val slackService: SlackService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ImdbParser::class.java)
        const val MAX_BODY_SIZE_15M = 15_000_000
    }


    @Scheduled(fixedRateString = "\${imdbparser.frequency}", initialDelayString = "\${imdbparser.delay}")
    fun parseImdb() {

        imdbParserConfig.userconfig.forEach { (username, userId) ->
            val imdbIds = getLatestMovieVotes(userId, username).map { it.imdbId }.toList()
            val existingRatings = ratingsRepository.findAllByNameAndImdbIdIn(username, imdbIds)

            val newRatings = getLatestMovieVotes(userId, username).filter { movie -> !existingRatings.contains(movie) }

            val molcsaRating = newRatings.filter { newRating ->
                existingRatings.any { existingRating -> newRating.imdbId == existingRating.imdbId && newRating.name == existingRating.name }
            }
            val nonMolcsaRatings = newRatings.filter { !molcsaRating.contains(it) }

            notifySlackWithMolcsaRatings(molcsaRating)

            LOGGER.info("New ratings: $nonMolcsaRatings")
            saveMoviesInTheDatabase(nonMolcsaRatings, username)

        }
    }

    private fun notifySlackWithMolcsaRatings(molcsaRating: List<Rating>) {
        molcsaRating.forEach {
            val movie = tmdbService.findMovieByImdbId(it.imdbId).movie_results.first()
            slackService.sendMessage(imdbParserConfig.channel, "${it.name} molcsavoted: ${movie.title}(${it.vote})")

        }
    }

    private fun getLatestMovieVotes(userid: String, username: String): List<Rating> {
        val get = Jsoup
                .connect("http://www.imdb.com/user/$userid/ratings?ref_=nv_usr_rt_4")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
                .maxBodySize(MAX_BODY_SIZE_15M)
                .get()

        val movieBase = get.select("#ratings-container div.lister-item.mode-detail")

        return movieBase.map {
            val imdbId = it.select("div.lister-item-image").first().attr("data-tconst")
            val vote = it.select("div.lister-item-content div.ipl-rating-widget div.ipl-rating-star--other-user span.ipl-rating-star__rating").first().text()
            val date = it.select("div.lister-item-content div.ipl-rating-widget + p").first().text().removePrefix("Rated on ")
            val zonedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd MMM uuuu"))
            Rating(imdbId = imdbId, vote = vote.toInt(), date = zonedDate, name = username)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveMoviesInTheDatabase(newRatings: List<Rating>, username: String) {
        newRatings
                .forEach { ratingsRepository.save(it) }

        if (newRatings.isNotEmpty()) {
            val titles = newRatings.map { tmdbService.findMovieByImdbId(it.imdbId).movie_results.first().title + "(${it.vote})" }.joinToString(separator = ", ") { it }
            slackService.sendMessage(imdbParserConfig.channel, "$username voted: $titles")
        }
    }

}
