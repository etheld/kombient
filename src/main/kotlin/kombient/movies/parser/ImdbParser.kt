package kombient.movies.parser

import kombient.movies.repository.Rating
import kombient.movies.repository.RatingsRepository
import kombient.movies.tmdb.TmdbService
import kombient.slack.SlackService
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.persistence.EntityManagerFactory

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
        val username: String
)

@Component
class ImdbParser(
        @Autowired private val imdbParserConfig: ImdbParserConfig,
        @Autowired private val entityManagerFactory: EntityManagerFactory,
        @Autowired private val ratingsRepository: RatingsRepository,
        @Autowired private val tmdbService: TmdbService,
        @Autowired private val slackService: SlackService
) {
    val MAX_BODY_SIZE_15M = 15_000_000

    @Scheduled(fixedRateString = "\${imdbparser.frequency}", initialDelayString = "\${imdbparser.delay}")
    fun parseImdb() {

        imdbParserConfig.userconfig.forEach { (username, userid) ->
            val get = Jsoup
                    .connect("http://www.imdb.com/user/$userid/ratings?ref_=nv_usr_rt_4")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
                    .maxBodySize(MAX_BODY_SIZE_15M)
                    .get()

            val movieBase = get.select("#ratings-container div.lister-item.mode-detail")


            val parsedMovies = movieBase.map {
                val imdbId = it.select("div.lister-item-image").first().attr("data-tconst")
                val vote = it.select("div.lister-item-content div.ipl-rating-widget div.ipl-rating-star--other-user span.ipl-rating-star__rating").first().text()
                val date = it.select("div.lister-item-content div.ipl-rating-widget + p").first().text().removePrefix("Rated on ")
                val zonedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd MMM uuuu"))
                ImdbParseMovieVote(imdbId, vote, zonedDate, username)
            }

            val userratings = ratingsRepository.findAllByNameAndImdbIdIn(username, parsedMovies.map { it.imdbId }.toList())

            val newRatings = parsedMovies.filter { movie -> userratings.none { it.date == movie.date && it.imdbId == movie.imdbId && it.name == movie.username && it.vote == movie.vote.toInt() } }

            println(newRatings)

            saveMoviesInTheDatabase(newRatings, username)


        }
    }

    @Transactional
    fun saveMoviesInTheDatabase(newRatings: List<ImdbParseMovieVote>, username: String) {
        val entityManager = entityManagerFactory.createEntityManager()

        newRatings
                .map { Rating(name = it.username, imdbId = it.imdbId, vote = it.vote.toInt(), date = it.date) }
                .forEach { entityManager.persist(it) }

        entityManager.flush()

        val titles = newRatings.map { tmdbService.findMovieByImdbId(it.imdbId).movie_results.first().title + "(${it.vote})" }.joinToString(separator = ", ") { it }

        if (newRatings.isNotEmpty()) {
            slackService.sendMessage(imdbParserConfig.channel, "$username voted: $titles")
        }
    }

}
