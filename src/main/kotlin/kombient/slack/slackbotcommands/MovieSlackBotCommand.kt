package kombient.slack.slackbotcommands

import kombient.movies.imdb.ImdbService
import org.springframework.stereotype.Component
import java.util.Optional

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Component
class MovieSlackBotCommand(
    private val imdbService: ImdbService
) : SlackBotCommand {

    private val commandRegex = Regex("!movie (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)

        if (match != null) {
            val (title) = match.destructured
            val movieByTitle = imdbService.getMovieByTitle(title)

            return Optional.of(String.format("%s", movieByTitle))
        }
        return Optional.empty()
    }
}
