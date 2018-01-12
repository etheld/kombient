package kombient.slack

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

data class Event(
        val type: String = "",
        val user: String = "",
        val text: String = "",
        val ts: String = "",
        val channel: String = "",
        val event_ts: String = ""
)

@NoArg
data class ChallengeEvent(
        val token: String = "",
        val challenge: String = "",
        val type: String = "",
        val team_id: String = "",
        val api_app_id: String = "",
        val event: Event = Event(),
        val event_id: String = "",
        val event_time: String = "",
        val authed_users: List<String> = ArrayList()
)

@RestController
class SlackEvent {
    @RequestMapping("/api/events")
    fun event(@RequestBody event: ChallengeEvent): String {
        println(event)
        return event.challenge
    }
}
