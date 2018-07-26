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

@Component
@Transactional
class ImdbParser(
    @Autowired private val imdbParserConfig: ImdbParserConfig,
    @Autowired private val ratingsRepository: RatingsRepository,
    @Autowired private val tmdbService: TmdbService,
    @Autowired private val slackService: SlackService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ImdbParser::class.java)
        const val MAX_BODY_SIZE_30M = 30_000_000
    }

    @Scheduled(fixedRateString = "\${imdbparser.frequency}", initialDelayString = "\${imdbparser.delay}")
    fun parseImdb() {

        imdbParserConfig.userconfig.forEach { (username, userId) ->
            //            val latestMovieVotes = getLatestMovieVotes(userId, username).take(10)
            val latestMovieVotes = getAllMovies(userId, username)

            val imdbIds = latestMovieVotes.map { it.imdbId }.toList()
            val existingRatings = ratingsRepository.findAllByNameAndImdbIdIn(username, imdbIds)

            val newRatings = latestMovieVotes.filter { movie ->
                !existingRatings.contains(movie)
            }

            val molcsaRatings = newRatings.filter { newRating ->
                existingRatings.any { existingRating -> newRating.imdbId == existingRating.imdbId && newRating.name == existingRating.name }
            }

            val nonMolcsaRatings = newRatings.filter {
                !molcsaRatings.contains(it)
            }

            LOGGER.info("New ratings: $nonMolcsaRatings")
            saveMoviesInTheDatabase(nonMolcsaRatings, molcsaRatings, username)
        }
    }

    private fun getAllMovies(userid: String, username: String): List<Rating> {

        var get = Jsoup
            .connect("http://www.imdb.com/user/$userid/ratings?ref_=nv_usr_rt_4")
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
            .maxBodySize(MAX_BODY_SIZE_30M)
            .get()

        val ratings: MutableList<Rating> = ArrayList()

        do {

            val movieBase = get.select("#ratings-container div.lister-item.mode-detail")

            ratings += movieBase.map {
                val imdbId = it.select("div.lister-item-image").first().attr("data-tconst")
                val vote = it.select("div.lister-item-content div.ipl-rating-widget div.ipl-rating-star--other-user span.ipl-rating-star__rating").first().text()
                val date = it.select("div.lister-item-content div.ipl-rating-widget + p").first().text().removePrefix("Rated on ")
                val zonedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd MMM uuuu"))
                Rating(imdbId = imdbId, vote = vote.toInt(), date = zonedDate, name = username)
            }
            val nextLink = get.select("a.next-page").attr("href")
            println(get.select("a.next-page"))
            get = Jsoup.connect("http://www.imdb.com/$nextLink")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
                .maxBodySize(MAX_BODY_SIZE_30M)
                .get()
        } while (nextLink != "#")
        return ratings
    }

    private fun getLatestMovieVotes(userid: String, username: String): List<Rating> {
        val get = Jsoup
            .connect("http://www.imdb.com/user/$userid/ratings?ref_=nv_usr_rt_4")
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
            .maxBodySize(MAX_BODY_SIZE_30M)
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
    fun saveMoviesInTheDatabase(nonMolcsaRatings: List<Rating>, molcsaRatings: List<Rating>, username: String) {
        ratingsRepository.saveAll(nonMolcsaRatings.plus(molcsaRatings))
//        nonMolcsaRatings
//            .plus(molcsaRatings)
//            .forEach { ratingsRepository.save(it) }
        ratingsRepository.flush()
//        if (molcsaRatings.isNotEmpty()) {
//            notifySlackWithRatings(molcsaRatings, username, "molcsa")
//        }
//        if (nonMolcsaRatings.isNotEmpty()) {
//            notifySlackWithRatings(nonMolcsaRatings, username, "")
//        }
    }

    private fun notifySlackWithRatings(molcsaRatings: List<Rating>, username: String, prefix: String) {
        val titles = molcsaRatings.map { tmdbService.getTitleByImdbId(it.imdbId) + "(${it.vote})" }.joinToString(separator = ", ") { it }
//        slackService.sendMessage(imdbParserConfig.channel, "$username ${prefix}voted: $titles")
    }
}
