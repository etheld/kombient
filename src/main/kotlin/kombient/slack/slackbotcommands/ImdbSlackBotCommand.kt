package kombient.slack.slackbotcommands

import kombient.movies.imdb.ImdbService
import kombient.movies.movieuserrating.MovieUserRatingService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbSlackBotCommand(
        private val imdbService: ImdbService,
        private val movieUserRatingService: MovieUserRatingService
) : SlackBotCommand {

    private val commandRegex = Regex("!imdb (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {

        val match = isMatched(message)
        if (match != null) {
            val (title) = match.destructured

            val imdbMovie = imdbService.getMovieByTitle(title)
            val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbMovie)

            return Optional.of(String.format("%s %s", imdbMovie, ratingText))

        }
        return Optional.empty();
    }
}
