package kombient.movies.imdb

import feign.Feign
import feign.Logger
import feign.gson.GsonDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ImdbService {

    @Value("\${omdbApiKey}")
    private lateinit var apiKey: String

    private var imdbClient = Feign.builder()
            .decoder(GsonDecoder())
            .logLevel(Logger.Level.FULL)
            .target(ImdbClient::class.java, "https://www.omdbapi.com/")

    fun getMovieById(imdbId: String): ImdbClient.ImdbMovie {
        return imdbClient.getMovieById(imdbId, apiKey)
    }
}
