package kombient.slack.slackbotcommands

import kombient.movies.imdb.ImdbService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbLastSlackBotCommand(
    private val imdbService: ImdbService
) : SlackBotCommand {

    private val commandRegex = Regex("!last(\\d+)? (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {

        val match = isMatched(message)

        if (match != null) {
            val (num, title) = match.destructured
            return Optional.ofNullable(imdbService.getLastMovieRatingsForUser(num.toIntOrNull() ?: 10, title))
        }
        return Optional.empty()
    }
}
