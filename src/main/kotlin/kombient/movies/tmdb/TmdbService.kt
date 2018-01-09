package kombient.movies.tmdb

import feign.Feign
import feign.Logger
import feign.gson.GsonDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TmdbService {

    @Value("\${tmdbApiKey}")
    private lateinit var apiKey: String

    private var tmdbClient = Feign.builder()
            .decoder(GsonDecoder())
            .logLevel(Logger.Level.FULL)
            .target(TmdbClient::class.java, "https://api.themoviedb.org")

    fun findMovie(title: String): TmdbClient.TmdbSearchResult {
        return tmdbClient.searchMovieByTitle(title, apiKey)
    }

    fun getMovieById(id: Int): TmdbClient.TmdbMovie {
        return tmdbClient.getMovieById(id, apiKey)
    }
}