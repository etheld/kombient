package kombient.slack

import kombient.movies.imdb.ImdbService
import kombient.movies.movieuserrating.MovieUserRatingService
import kombient.movies.tmdb.TmdbService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.Executors


data class Event(
        val type: String = "",
        val user: String = "",
        val text: String = "",
        val ts: String = "",
        val channel: String = "",
        val event_ts: String = ""
)

data class SlackEvent(
        val token: String = "",
        val challenge: String = "",
        val type: String = "",
        val team_id: String = "",
        val api_app_id: String = "",
        val event: Event = Event(),
        val event_id: String = "",
        val event_time: String = "",
        val authed_users: List<String> = ArrayList()
)

@RestController
class SlackController {

    @Autowired
    private lateinit var imdbService: ImdbService

    @Autowired
    private lateinit var tmdbService: TmdbService

    @Autowired
    private lateinit var movieUserRatingService: MovieUserRatingService

    @Autowired
    private lateinit var slackClient: SlackClient

    val executor = Executors.newFixedThreadPool(5)

    @RequestMapping("/api/events")
    fun event(@RequestBody event: SlackEvent): String {

        val regex = Regex("!imdb (.+)")
        val match = regex.matchEntire(event.event.text)
        executor.submit({
            if (match != null) {
                val (title) = match.destructured

                val movieSearchResult = tmdbService.findMovie(title)

                val tmdbMovie = tmdbService.getMovieById(movieSearchResult.results.first().id)
                val imdbMovie = imdbService.getMovieById(tmdbMovie.imdb_id)

                val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbMovie)

                val messageFormat = String.format("[IMDb] %s(%d) %.1f/10 from %d votes %s [%s] http://www.imdb.com/title/%s %s",
                        imdbMovie.Title,
                        imdbMovie.Year,
                        imdbMovie.imdbRating,
                        imdbMovie.imdbVotes.replace(",", "").toInt(),
                        imdbMovie.Runtime,
                        imdbMovie.Genre,
                        imdbMovie.imdbID,
                        ratingText)

                slackClient.sendMessage(event.event.channel, messageFormat)

            }
        })

        return event.challenge
    }
}
