package kombient.movies.imdb

import kombient.movies.repository.RatingsRepository
import kombient.movies.tmdb.TmdbService
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@RefreshScope
@Component
class ImdbService(
        val imdbClient: ImdbClient,
        val tmdbService: TmdbService,
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

    fun getMovie(title: String): ImdbClient.ImdbMovie {

        val searchResult = tmdbService.findMovie(title)
        val (_, imdbId) = tmdbService.getMovieById(searchResult.results.first().id)

        return getMovieById(imdbId)

    }

}
