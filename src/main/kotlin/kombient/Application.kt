package kombient

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Logger
import kombient.movies.parser.ImdbParserConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
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
@EntityScan(basePackages = ["kombient"])
class Application {

    @Bean
    fun placeholderConfigurer(): PropertySourcesPlaceholderConfigurer {
        val propsConfig = PropertySourcesPlaceholderConfigurer()
        addResourceIfExists(propsConfig, "git.properties")
        addResourceIfExists(propsConfig, "META-INF/build-info.properties")
        propsConfig.setIgnoreResourceNotFound(false)
        propsConfig.setIgnoreUnresolvablePlaceholders(true)
        return propsConfig
    }

    private fun addResourceIfExists(propsConfig: PropertySourcesPlaceholderConfigurer, propertyPath: String) {
        val propertyResource = ClassPathResource(propertyPath)
        if (propertyResource.exists()) {
            propsConfig.setLocation(propertyResource)
        }
    }

    @Bean
    @Primary
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
        filter.setAfterMessagePrefix("REQUEST DATA : ")
        return filter
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
