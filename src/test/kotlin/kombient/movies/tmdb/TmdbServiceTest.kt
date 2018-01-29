package kombient.movies.tmdb

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
internal class TmdbServiceTest {

    @Autowired
    private lateinit var tmdbService: TmdbService

    @Test
    fun shouldFindFightClub() {

        stubFor(
                get(
                        urlEqualTo("/3/search/movie?language=en-US&page=1&include_adult=false")).willReturn(
                        aResponse().withBody("x")
                )
        )

        val findMovie = tmdbService.findMovie("fight club")
        val results = findMovie.results
        assertThat(results).extracting("title").contains("Fight Club")
        assertThat(results).extracting("id").contains(550)
    }

    @Test
    fun shouldFindFightClubImdbId() {
        val findMovie = tmdbService.findMovie("fight club")
        val id = findMovie.results.first().id
        val movie = tmdbService.getMovieById(id)

        assertThat(movie.imdb_id).isEqualToIgnoringCase("tt0137523")


    }

}

@Configuration
@EnableAutoConfiguration
@EnableFeignClients
@ComponentScan(basePackages = ["kombient.movies.imdb"])
class TmdbServiceConfig {
}
//    @Bean
//    fun tmdbService() {
//        return TmdbService()
//    }
//
//    @Bean
//    fun tmdbClient() {
//        return TmdbClient()
//    }
//
//    fun main(args: Array<String>) {
//        runApplication<Application>(*args)
//    }
//
//}



//object TmdbSpek : Spek({
//    describe("tmdbservice") {
//
//        val tmdbClient: TmdbClient = Mockito.mock(TmdbClient::class.java)
//
//        val tmdbService = TmdbService(tmdbClient)
//
//
//        it("tests calls") {
//
//        }
//
//    }
//}
//)
//
