package kombient.movies.imdb

import kombient.movies.repository.RatingsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@RefreshScope
@Component
class ImdbService(
        val imdbClient: ImdbClient,
        val ratingsRepository: RatingsRepository
) {
    @Value("\${omdbApiKey}")
    private lateinit var apiKey: String

    fun getMovieById(imdbId: String): ImdbClient.ImdbMovie {
        return imdbClient.getMovieById(imdbId, apiKey)
    }

    fun getLastMovieRatingsForUser(num: Int, user: String): String {
        val findLastVotesForUser = ratingsRepository.findLastVotesForUser(user, PageRequest.of(1, num))

        if (findLastVotesForUser.isEmpty()) {
            return "Could not find any ratings for $user"
        }
        return findLastVotesForUser.joinToString(separator = ", ") { rating ->
            String.format("%s (%d)", rating.title.title, rating.vote)
        }
    }
}
