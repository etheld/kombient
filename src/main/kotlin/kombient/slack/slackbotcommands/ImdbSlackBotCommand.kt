package kombient.slack.slackbotcommands

import kombient.movies.movieuserrating.MovieUserRatingService
import kombient.movies.tmdb.TmdbService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbSlackBotCommand(
        private val tmdbService: TmdbService,
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

            val firstSearchResult = tmdbService.findMovie(title).results.firstOrNull() ?: return Optional.of("Could not find a match for this title")

            val movie = tmdbService.getMovieById(firstSearchResult.id)

            val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(movie.imdb_id)

            return Optional.of(String.format("%s %s", movie, ratingText))

        }
        return Optional.empty()
    }
}
