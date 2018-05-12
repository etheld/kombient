package kombient.movies.tmdb

import com.google.common.util.concurrent.RateLimiter
import kombient.movies.tmdb.TmdbClient.TmdbExternalIds
import kombient.movies.tmdb.TmdbClient.TmdbFindResult
import kombient.movies.tmdb.TmdbClient.TmdbMovieSearchResult
import kombient.movies.tmdb.TmdbClient.TmdbMultiSearchResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TmdbService(val tmdbClient: TmdbClient) {

    @Value("\${tmdbApiKey}")
    private lateinit var apiKey: String

    val rateLimiter = RateLimiter.create(3.5)!!

    fun findMovieByImdbId(id: String): TmdbFindResult {
        rateLimiter.acquire()
        return tmdbClient.findByImdbId(id, apiKey)
    }

    fun findMulti(title: String): TmdbMultiSearchResult {
        rateLimiter.acquire()
        return tmdbClient.findMulti(title, apiKey)
    }

    fun findMovie(title: String): TmdbMovieSearchResult {
        rateLimiter.acquire()
        return tmdbClient.findMovieByTitle(title, apiKey)
    }

    fun getMovieById(id: Int): TmdbClient.TmdbMovie {
        rateLimiter.acquire()
        return tmdbClient.getMovieById(id, apiKey)
    }

    fun getTvById(id: Int): TmdbClient.TmdbTv {
        rateLimiter.acquire()
        return tmdbClient.getTvById(id, apiKey)
    }

    fun getImdbIdByTvId(id: Int): TmdbExternalIds {
        rateLimiter.acquire()
        return tmdbClient.getImdbIdFromTvId(id, apiKey)
    }

    fun getTitleByImdbId(id: String): String {
        val results = findMovieByImdbId(id)
        return when {
            !results.movie_results.isEmpty() -> results.movie_results.first().title
            !results.tv_results.isEmpty() -> results.tv_results.first().name
            else -> "unknown"
        }
    }

    fun findTV(title: String): TmdbClient.TmdbTVSearchResult {
        return tmdbClient.findTVByTitle(title, apiKey)
    }
}
