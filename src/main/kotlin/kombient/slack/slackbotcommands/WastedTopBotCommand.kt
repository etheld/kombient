package kombient.slack.slackbotcommands

import kombient.movies.moviemetadata.MovieMetaDataRepository
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Optional

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Component
class WastedTopBotCommand(
    private val movieMetaDataRepository: MovieMetaDataRepository
) : SlackBotCommand {

    private val command = "!wasted"
    private val commandRegex = Regex("$command(\\d+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)
        if (match != null) {
            val (topX) = match.destructured

            val allMoviesMetaData = movieMetaDataRepository.findTopWasted(PageRequest.of(0, topX.toIntOrNull() ?: 10))
            val topList = allMoviesMetaData.joinToString(separator = "\n") { "${it["name"]} -> (${DurationFormatter(Duration.of(it.get("totaltime").toString().toLong(), ChronoUnit.MINUTES))})" }

            return Optional.of(String.format("Wasted toplist: %s", topList))
        }
        return Optional.of("Could not find the user")
    }
}
