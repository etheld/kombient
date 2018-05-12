package kombient.movies.imdb

import kombient.movies.tmdb.TmdbService
import org.springframework.beans.factory.annotation.Autowired

//@SpringBootTest(classes = [Application::class])
//@ActiveProfiles("dev")
//@ExtendWith(SpringExtension::class)
internal class TmdbServiceTest {

    @Autowired
    private lateinit var tmdbService: TmdbService

//    @Disabled("rethink it")
//    @Test
//    fun shouldFindFightClub() {
//        val findMovie = tmdbService.findMovie("fight club")
//        val results = findMovie.results
//        assertThat(results).extracting("title").contains("Fight Club")
//        assertThat(results).extracting("id").contains(550)
//    }
//
//    @Disabled("rethink it")
//    @Test
//    fun shouldFindFightClubImdbId() {
//        val findMovie = tmdbService.findMovie("fight club")
//        val id = findMovie.results.first().id
//        val movie = tmdbService.getMovieById(id)
//
//        assertThat(movie.imdb_id).isEqualToIgnoringCase("tt0137523")
//    }
}
