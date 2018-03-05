package kombient.movies.tmdb

import com.google.common.util.concurrent.RateLimiter
import kombient.movies.tmdb.TmdbClient.MediaType.MOVIE
import kombient.movies.tmdb.TmdbClient.MediaType.TV
import kombient.movies.tmdb.TmdbClient.TmdbExternalIds
import kombient.movies.tmdb.TmdbClient.TmdbFindResult
import kombient.movies.tmdb.TmdbClient.TmdbMultiSearchResult
import kombient.movies.tmdb.TmdbClient.TmdbMultiSearchSummary
import kombient.movies.tmdb.TmdbClient.TmdbSearchResult
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

    fun findMovie(title: String): TmdbSearchResult {
        rateLimiter.acquire()
        return tmdbClient.searchMovieByTitle(title, apiKey)
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

    fun formatMulti(multiSearchResult: TmdbMultiSearchSummary): String {
        return when {
            multiSearchResult.media_type == TV -> getTvById(multiSearchResult.id).toString()
            multiSearchResult.media_type == MOVIE -> getMovieById(multiSearchResult.id).toString()
            else -> ""
        }
    }

    fun getImdbIdFromMultiResult(searchResult: TmdbMultiSearchSummary): String {
        return when {
            searchResult.media_type == MOVIE -> {
                getMovieById(searchResult.id).imdb_id
            }
            searchResult.media_type == TV -> {
                val tvId = getTvById(searchResult.id).id
                getImdbIdByTvId(tvId).imdb_id
            }
            else -> throw IllegalStateException("mediatype is not tv or movie")
        }
    }

    fun getTitleByImdbId(id: String): String {
        val results = findMovieByImdbId(id)
        return if (results.movie_results.isEmpty()) {
            results.tv_results.first().name
        } else {
            results.movie_results.first().title
        }
    }

}
