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

            val findMulti = tmdbService.findMulti(title)

            if (findMulti.results.isNotEmpty()) {
                val firstResult = findMulti.results.first()

                val imdbId = tmdbService.getImdbIdFromMultiResult(firstResult)

                val multiSearchResultDetails = String.format("%s https://imdb.com/title/%s", tmdbService.formatMulti(firstResult), imdbId)

                val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbId)
                return Optional.of(String.format("%s %s", multiSearchResultDetails, ratingText))
            }

        }
        return Optional.empty()
    }
}
