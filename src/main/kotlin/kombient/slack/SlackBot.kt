//package kombient.slack
//
//import kombient.movies.imdb.ImdbService
//import kombient.movies.movieenricher.MovieUserRatingService
//import kombient.movies.tmdb.TmdbService
//import me.ramswaroop.jbot.core.slack.Bot
//import me.ramswaroop.jbot.core.slack.Controller
//import me.ramswaroop.jbot.core.slack.EventType
//import me.ramswaroop.jbot.core.slack.models.Event
//import me.ramswaroop.jbot.core.slack.models.Message
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.stereotype.Component
//import org.springframework.web.socket.WebSocketSession
//import java.util.regex.Matcher
//
//@Component
//class SlackBot : Bot() {
//
//    @Autowired
//    private lateinit var imdbService: ImdbService
//
//    @Autowired
//    private lateinit var tmdbService: TmdbService
//
//
//    @Autowired
//    private lateinit var movieUserRatingService: MovieUserRatingService
//
//
//    @Value("\${slackBotToken}")
//    private var slackBotToken = ""
//
//    override fun getSlackToken(): String {
//        return slackBotToken
//    }
//
//    override fun getSlackBot(): Bot {
//        return this
//    }
//
//
//    @Controller(events = [EventType.MESSAGE], pattern = "^!imdb (.*)")
//    fun onImdb(session: WebSocketSession, event: Event, matcher: Matcher) {
//        val movieTitleQuery = matcher.group(1)
//
//        val movieSearchResult = tmdbService.findMovie(movieTitleQuery)
//
//        val tmdbMovie = tmdbService.getMovieById(movieSearchResult.results.first().id)
//        val imdbMovie = imdbService.getMovieById(tmdbMovie.imdb_id)
//
//        val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbMovie)
//
//        val messageFormat = String.format("[IMDb] %s(%d) %.1f/10 from %d votes %s [%s] http://www.imdb.com/title/%s %s",
//                imdbMovie.Title,
//                imdbMovie.Year,
//                imdbMovie.imdbRating,
//                imdbMovie.imdbVotes.replace(",", "").toInt(),
//                imdbMovie.Runtime,
//                imdbMovie.Genre,
//                imdbMovie.imdbID,
//                ratingText)
//
//        val message = Message(messageFormat)
//
//        reply(session, event, message)
//    }
//
////    @Controller(events = [EventType.MESSAGE], pattern = "^!im (tt.*)")
////    fun onImdbTT(session: WebSocketSession, event: Event, matcher: Matcher) {
////        reply(session, event, Message("yolo"))
////        if (session.id == "0") {
////            reply(session, event, Message("bu"))
////            println(session)
////            println(event)
////            val group: String = matcher.group(1)
////            reply(session, event, Message(group))
////            imdbService.getMovieById(matcher.group(1))
////        }
////        stopConversation(event)
////    }
//
//}
