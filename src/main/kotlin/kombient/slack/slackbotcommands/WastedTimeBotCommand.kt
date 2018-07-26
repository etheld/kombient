package kombient.slack.slackbotcommands

import kombient.movies.moviemetadata.MovieMetaDataRepository
import org.hibernate.validator.internal.util.logging.formatter.DurationFormatter
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.Optional

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Component
class WastedTimeBotCommand(
    private val movieMetaDataRepository: MovieMetaDataRepository
) : SlackBotCommand {

    private val command = "!wasted"
    private val commandRegex = Regex("$command (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)
        if (match != null) {
            val (name) = match.destructured

            val allMoviesMetaData = movieMetaDataRepository.findAllMetaDataByVotersName(name)
            val totalRuntime = allMoviesMetaData.map { it.runTime }.reduce { acc, runtime -> acc + runtime }

            return Optional.of(String.format("%s wasted: %s", name, DurationFormatter(Duration.of(totalRuntime, ChronoUnit.MINUTES))))
        }
        return Optional.of("Could not find the user")
    }

}
