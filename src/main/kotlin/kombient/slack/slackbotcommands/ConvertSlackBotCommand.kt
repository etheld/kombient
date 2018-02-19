package kombient.slack.slackbotcommands

import kombient.convert.ConvertService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ConvertSlackBotCommand(
        private val convertService: ConvertService
) : SlackBotCommand {

    private val commandMatchRegex = Regex("!convert (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandMatchRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)

        if (match != null) {
            val (input: String) = match.destructured
            return Optional.ofNullable(convertService.convert(input))
        }

        return Optional.empty()
    }

}
