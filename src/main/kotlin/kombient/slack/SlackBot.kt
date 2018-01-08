package kombient.slack

import me.ramswaroop.jbot.core.slack.Bot
import me.ramswaroop.jbot.core.slack.Controller
import me.ramswaroop.jbot.core.slack.EventType
import me.ramswaroop.jbot.core.slack.models.Event
import me.ramswaroop.jbot.core.slack.models.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.regex.Matcher
import kombient.imdb.ImdbService
import kombient.movieenricher.MovieUserRatingService
import kombient.tmdb.TmdbService

@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties
class SlackBot : Bot() {

    @Autowired
    private lateinit var imdbService: ImdbService

    @Autowired
    private lateinit var tmdbService: TmdbService


    @Autowired
    private lateinit var movieUserRatingService: MovieUserRatingService


    @Value("\${slackBotToken}")
    private var slackBotToken = ""

    override fun getSlackToken(): String {
        return slackBotToken
    }

    override fun getSlackBot(): Bot {
        return this
    }


    @Controller(events = [EventType.MESSAGE], pattern = "^!imdb (.*)")
    fun onImdb(session: WebSocketSession, event: Event, matcher: Matcher) {
        if (session.id == "0") {
            val movieTitleQuery = matcher.group(1)

            val movieSearchResult = tmdbService.findMovie(movieTitleQuery)

            val tmdbMovie = tmdbService.getMovieById(movieSearchResult.results.first().id)
            val imdbMovie = imdbService.getMovieById(tmdbMovie.imdb_id)

            val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbMovie)

            val message = Message(String.format("[IMDb] %s(%d) %.1f/10 from %d votes %s [%s] http://www.imdb.com/title/%s %s", imdbMovie.Title, imdbMovie.Year, imdbMovie.imdbRating, imdbMovie.imdbVotes.replace(",","").toInt(), imdbMovie.Runtime, imdbMovie.Genre, imdbMovie.imdbID, ratingText))

            reply(session, event, message)
        }
    }

    @Controller(events = [EventType.MESSAGE], pattern = "^!im (tt.*)")
    fun onImdbTT(session: WebSocketSession, event: Event, matcher: Matcher) {
//        reply(session, event, Message("yolo"))
//        if (session.id == "0") {
//            reply(session, event, Message("bu"))
//            println(session)
//            println(event)
//            val group: String = matcher.group(1)
//            reply(session, event, Message(group))
//            imdbService.getMovieById(matcher.group(1))
//        }
//        stopConversation(event)
    }

}