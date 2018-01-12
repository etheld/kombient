package kombient.slack

import feign.Feign
import feign.RequestLine
import feign.gson.GsonDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 *
 * @author pevarsanyi@expedia.com
 */

interface SlackAPI {

    @RequestLine("POST /api/chat.postMessage?token={token}&channel={channel}&text={text}")
    fun postMessage(token: String, channel: String, text: String)
}

@Component
class SlackClient {

    @Value("\${slackBotToken}")
    private lateinit var slackToken: String

    private val api = Feign.builder()
            .decoder(GsonDecoder())
            .target(SlackAPI::class.java, "https://slack.com")

    fun sendMessage(channel: String, text: String) {
        api.postMessage(slackToken, channel, text)
    }

}
