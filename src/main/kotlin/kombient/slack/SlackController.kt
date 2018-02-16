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
                try {

                    val response = botCommand.process(event.event.text)
                    response
                            .map { slackService.sendMessage(event.event.channel, it) }
                            .orElse(slackService.sendMessage(event.event.channel, "Something just broke"))
                } catch (e: Exception) {
                    LOGGER.error("Error found: ", e)
                    slackService.sendMessage(event.event.channel, "Sorry there is a problem with this service")
                }
            }
        })

        return event.challenge
    }
}
