package kombient.movieenricher

import com.google.common.base.Joiner
import kombient.imdb.ImdbClient
import kombient.repository.RatingsRepository
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