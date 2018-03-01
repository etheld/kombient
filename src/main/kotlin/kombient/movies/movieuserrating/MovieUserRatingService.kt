package kombient.movies.movieuserrating

import com.google.common.base.Joiner
import kombient.movies.repository.RatingsRepository
import org.springframework.stereotype.Component

@Component
class MovieUserRatingService(
        val ratingRepository: RatingsRepository
) {

    fun getUserRatingsForImdbMovie(imdbId: String): String {
        val ratingList = ratingRepository.findByImdbId(imdb_id = imdbId).map { r -> String.format("%s voted %s", r.name, r.vote) }
        return Joiner.on(", ").join(ratingList)
    }


}
