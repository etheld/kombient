package kombient.movies.imdb

import feign.Feign
import feign.Logger
import feign.gson.GsonDecoder
import kombient.movies.repository.RatingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ImdbService {

    @Value("\${omdbApiKey}")
    private lateinit var apiKey: String

    @Autowired
    private lateinit var ratingRepository: RatingsRepository

    private var imdbClient = Feign.builder()
            .decoder(GsonDecoder())
            .logLevel(Logger.Level.FULL)
            .target(ImdbClient::class.java, "https://www.omdbapi.com/")

    fun getMovieById(imdbId: String): ImdbClient.ImdbMovie {
        return imdbClient.getMovieById(imdbId, apiKey)
    }

    fun getLastMovieRatingsForUser(user: String): String {
        return ratingRepository.findLastVotesForUser(user, PageRequest.of(1, 10))
                .joinToString(separator = ",") { rating ->
                    String.format("%s %d", rating.title.title, rating.vote)
                }
    }
}
