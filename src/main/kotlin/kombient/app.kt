package kombient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.filter.CommonsRequestLoggingFilter




@SpringBootApplication(scanBasePackages = ["kombient", "me.ramswaroop.jbot"])
open class Application {
    @Bean
    open fun logFilter(): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        filter.setMaxPayloadLength(10000)
        filter.isIncludeHeaders = false
        filter.setAfterMessagePrefix("REQUEST DATA : ")
        return filter
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
