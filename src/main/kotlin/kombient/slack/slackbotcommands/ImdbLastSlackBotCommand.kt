package kombient.slack.slackbotcommands

import kombient.movies.imdb.ImdbService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbLastSlackBotCommand(
        val imdbService: ImdbService
) : SlackBotCommand {

    override fun process(message: String): Optional<String> {

        val imdbLastMatch = Regex("!last(\\d+)? (.+)").matchEntire(message)

        if (imdbLastMatch != null) {
            val (num, title) = imdbLastMatch.destructured
            return Optional.ofNullable(imdbService.getLastMovieRatingsForUser(num.toIntOrNull() ?: 10, title))
        }
        return Optional.empty()
    }

}
