package kombient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.orm.hibernate5.SessionFactoryUtils
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean



@SpringBootApplication(scanBasePackages = ["kombient", "me.ramswaroop.jbot"])
//@EnableConfigurationProperties
//@PropertySource("classpath: application.yml")
//@SpringBootApplication
open class Application {
//
//    @Bean
//    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
//        val em = LocalContainerEntityManagerFactoryBean()
//        em.dataSource = dataSource()
//        em.setPackagesToScan(*arrayOf("org.baeldung.persistence.model"))
//
//        val vendorAdapter = HibernateJpaVendorAdapter()
//        em.jpaVendorAdapter = vendorAdapter
//        em.setJpaProperties(additionalProperties())
//
//        return em
//    }
//    @Bean
//    fun propertyConfigurer() = PropertySourcesPlaceholderConfigurer().apply {
//        setPlaceholderPrefix("%{")
//    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}