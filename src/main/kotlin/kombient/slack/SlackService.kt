package kombient.slack

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 *
 * @author pevarsanyi@expedia.com
 */

@FeignClient(name = "slack", url = "https://slack.com")
interface SlackClient {

    @PostMapping("/api/chat.postMessage")
    fun postMessage(
            @RequestParam("token") token: String,
            @RequestParam("channel") channel: String,
            @RequestParam("text") text: String)

    @GetMapping("/api/users.info")
    fun getUser(
            @RequestParam("token") token: String,
            @RequestParam("user") user: String): SlackUserResponse

}

data class SlackUserResponse(
        val ok: Boolean = false,
        val user: UserInfo = UserInfo()
)

data class UserInfo(
        val name: String = ""
)

@Component
class SlackService(
        val client: SlackClient
) {

    @Value("\${slackBotToken}")
    private lateinit var slackToken: String

    fun sendMessage(channel: String, text: String) {
        client.postMessage(slackToken, channel, text)
    }

    fun getUser(username: String): SlackUserResponse {
        return client.getUser(slackToken, username)
    }

}
