package kombient.slack.data

import java.util.ArrayList

data class SlackEvent(
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

data class Event(
        val type: String = "",
        val user: String = "",
        val text: String = "",
        val ts: String = "",
        val channel: String = "",
        val event_ts: String = ""
)

