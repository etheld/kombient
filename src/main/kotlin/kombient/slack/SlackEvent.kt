package kombient.slack

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@NoArg
data class ChallengeEvent(val token: String, val challenge: String, val type: String)

@RestController
class SlackEvent {

    @RequestMapping("/api/events")
    fun event(@RequestBody event: ChallengeEvent): String {
        return event.challenge
    }
}
