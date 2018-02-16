package kombient.slack.slackbotcommands

import kombient.movies.imdb.ImdbService
import kombient.movies.movieuserrating.MovieUserRatingService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbSlackBotCommand(
        val imdbService: ImdbService,
        val movieUserRatingService: MovieUserRatingService
) : SlackBotCommand {

    override fun process(message: String): Optional<String> {

        val imdbMatch = Regex("!imdb (.+)").matchEntire(message)
        if (imdbMatch != null) {
            val (title) = imdbMatch.destructured

            val imdbMovie = imdbService.getMovie(title)
            val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbMovie)

            return Optional.of(String.format("%s %s", imdbMovie, ratingText))

        }
        return Optional.empty();
    }
}
