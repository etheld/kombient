package kombient.slack.slackbotcommands

import java.util.Optional

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
interface SlackBotCommand {
    fun process(message: String): Optional<String>
}
