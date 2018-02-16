package kombient.slack.slackbotcommands

import kombient.movies.imdb.ImdbService
import java.util.Optional

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
class MovieSlackBotCommand(
        val imdbService: ImdbService
) : SlackBotCommand {

    override fun process(message: String): Optional<String> {
        val movieMatch = Regex("!movie (.+)").matchEntire(message)
        if (movieMatch != null) {
            val (title) = movieMatch.destructured
            imdbService.getMovie(title)

        }
        return Optional.empty()
    }

}
