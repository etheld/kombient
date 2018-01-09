package kombient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["kombient", "me.ramswaroop.jbot"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
