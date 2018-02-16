package kombient.slack.slackbotcommands

import kombient.convert.ConvertService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ConvertSlackBotCommand(
        val convertService: ConvertService
) : SlackBotCommand {

    override fun process(message: String): Optional<String> {
        val convertMatch = Regex("!convert (.+)").matchEntire(message)

        if (convertMatch != null) {
            val (input: String) = convertMatch.destructured
            return Optional.ofNullable(convertService.convert(input))
        }

        return Optional.empty()
    }

}
