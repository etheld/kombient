package kombient.movies.tmdb

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TmdbService(val tmdbClient: TmdbClient) {

    @Value("\${tmdbApiKey}")
    private lateinit var apiKey: String

    fun findMovie(title: String): TmdbClient.TmdbSearchResult {
        return tmdbClient.searchMovieByTitle(title, apiKey)
    }

//    fun findTVseries(title: String): TmdbClient.TmdbSearchResult {
//        return tmdbClient.searchTVSeriesByTitle(title, apiKey)
//    }

    fun getMovieById(id: Int): TmdbClient.TmdbMovie {
        return tmdbClient.getMovieById(id, apiKey)
    }

//    fun getTV(id: Int): TmdbClient.TmdbSearchResult {
//        return tmdbClient.getTVById(id, apiKey)
//    }

//    fun getImdbId(title: String): Int {
//        val findMovie = findMovie(title)
//
//        if (findMovie.results.size == 0) {
//            val findTVseries = findTVseries(title)
//
//        }
//    }

}
