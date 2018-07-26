package kombient.movies.movieuserrating

import com.google.common.base.Joiner
import kombient.movies.moviemetadata.MovieMetaDataRepository
import kombient.movies.repository.RatingsRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class MovieUserRatingService(
    private val ratingRepository: RatingsRepository,
    private val movieMetaDataRepository: MovieMetaDataRepository
) {

    fun getUserRatingsForImdbMovie(imdbId: String): String {
        val userRatingList = ratingRepository.findByImdbId(imdbId).map { r -> String.format("%s voted %s", r.name, r.vote) }
        return Joiner.on(", ").skipNulls().join(listOf(getImdbRating(imdbId)).plus(userRatingList))
    }

    private fun getImdbRating(imdbId: String): String {
        val movieMetaData = movieMetaDataRepository.findByImdbId(imdbId)
        return "imdb: ${movieMetaData?.imdbRating ?: "NA"}(${movieMetaData?.imdbVotes ?: "NA"})"
    }

    fun getImdbTopX(topX: Int): List<Map<String, String>> {
        return ratingRepository.findTopXRaters(PageRequest.of(0, topX))
    }
}
