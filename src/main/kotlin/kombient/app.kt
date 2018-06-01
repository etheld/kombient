package kombient

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Logger
import kombient.movies.parser.ImdbParserConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.ClassPathResource
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.zalando.logbook.Conditions
import org.zalando.logbook.JsonHttpLogFormatter
import org.zalando.logbook.Logbook

@SpringBootApplication(scanBasePackages = ["kombient"])
@EnableConfigurationProperties(ImdbParserConfig::class)
@EnableFeignClients
@EnableRetry
@EnableScheduling
@EnableTransactionManagement
class Application {

    @Bean
    fun placeholderConfigurer(): PropertySourcesPlaceholderConfigurer {
        val propsConfig = PropertySourcesPlaceholderConfigurer()
        propsConfig.setLocation(ClassPathResource("git.properties"))
        propsConfig.setLocation(ClassPathResource("META-INF/build-info.properties"))
        propsConfig.setIgnoreResourceNotFound(false)
        propsConfig.setIgnoreUnresolvablePlaceholders(true)
        return propsConfig
    }

    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.corePoolSize = 5
        threadPoolTaskExecutor.maxPoolSize = 10
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true)
        return threadPoolTaskExecutor
    }

    @Bean
    fun logger(mapper: ObjectMapper): Logbook {
        return Logbook.builder()
            .condition(Conditions.requestTo("/api/**"))
            .formatter(JsonHttpLogFormatter())
            .build()
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }

    @Bean
    fun logFilter(): CommonsRequestLoggingFilter {
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
