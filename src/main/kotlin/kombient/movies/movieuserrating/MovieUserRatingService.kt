package kombient.movies.movieuserrating

import com.google.common.base.Joiner
import kombient.movies.imdb.ImdbClient
import kombient.movies.repository.RatingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MovieUserRatingService {

    @Autowired
    private lateinit var ratingRepository : RatingsRepository

    fun getUserRatingsForImdbMovie(imdbMovie: ImdbClient.ImdbMovie): String {
        val ratingList = ratingRepository.findByImdbId(imdb_id = imdbMovie.imdbID).map { r -> String.format("%s voted %s", r.name, r.vote) }
        return Joiner.on(", ").join(ratingList)
    }

}
