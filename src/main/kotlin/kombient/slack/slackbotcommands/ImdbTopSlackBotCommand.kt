package kombient.slack.slackbotcommands

import kombient.movies.movieuserrating.MovieUserRatingService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbTopSlackBotCommand(
        private val userRatingService: MovieUserRatingService
) : SlackBotCommand {
    private val commandRegex = Regex("!imdbtop(\\d+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)
        if (match != null) {
            val (topX) = match.destructured
            val imdbTopX = userRatingService.getImdbTopX(topX.toIntOrNull() ?: 10)
            val joinToString = imdbTopX
                    .sortedBy { it["values"] }
                    .joinToString(separator = ", ", transform = { "${it["name"]}(${it["votes"].toString()})" })
            return Optional.of(joinToString)
        }
        return Optional.empty()
    }

}
