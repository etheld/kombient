package kombient.movies.imdb

import kombient.Application
import kombient.movies.imdb.tmdb.TmdbService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@SpringBootTest(classes = [Application::class])
@ExtendWith(SpringExtension::class)
internal class TmdbServiceTest {


    @Autowired
    private lateinit var tmdbService: TmdbService

    @Test
    fun shouldFindFightClub() {
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