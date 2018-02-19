package kombient.slack

import kombient.slack.data.SlackEvent
import kombient.slack.slackbotcommands.SlackBotCommand
import org.slf4j.LoggerFactory
import org.springframework.core.task.TaskExecutor
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SlackController(
        val slackService: SlackService,
        val executor: TaskExecutor,
        val botCommands: List<SlackBotCommand>
) {
    private val LOGGER = LoggerFactory.getLogger(SlackController::class.java)

    @RequestMapping("/api/events")
    fun event(@RequestBody event: SlackEvent): String {
        executor.execute({
            for (botCommand in botCommands) {
                val channel = event.event.channel
                val message = event.event.text

                try {

                    val commandMatch = botCommand.isMatched(message)

                        LOGGER.info("{} is {}({})", botCommand, commandMatch, commandMatch != null)
                    if (commandMatch != null) {
                        val response = botCommand.process(message)
                        LOGGER.info("Response is: {}", response)
                        response
                                .map { slackService.sendMessage(channel, it) }
                                .orElse(slackService.sendMessage(channel, "Something just broke"))

                    }
                } catch (e: Exception) {
                    LOGGER.error("Error found: ", e)
                    slackService.sendMessage(channel, "Sorry there is a problem with this service")
                }
            }
        })

        return event.challenge
    }
}
