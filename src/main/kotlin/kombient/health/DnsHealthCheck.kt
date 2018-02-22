package kombient.health

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import java.net.InetAddress

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Component
class DnsHealthCheck(
        @Value("dns.health.hostnames") private val hostnames: List<String>
) : HealthIndicator {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DnsHealthCheck::class.java)
    }

    override fun health(): Health {
        return try {
            hostnames.forEach {
                InetAddress.getByName(it)
            }
            Health.up().build()
        } catch (e: Exception) {
            LOGGER.error("Aw, dns resolution failed.", e)
            Health.down().build()
        }
    }

}
