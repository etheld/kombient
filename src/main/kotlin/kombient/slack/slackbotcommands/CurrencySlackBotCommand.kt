package kombient.slack.slackbotcommands

import kombient.currency.CurrencyService
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CurrencySlackBotCommand(
    private val currencyService: CurrencyService
) : SlackBotCommand {

    private val commandRegex = Regex("!([a-zA-Z]{3}) (\\d+) ([a-zA-Z]{3})")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)

        if (match != null) {
            val (to, amount, from) = match.destructured
            return Optional.ofNullable(currencyService.convert(from.toUpperCase(), to.toUpperCase(), amount))
        }

        return Optional.empty()
    }
}
