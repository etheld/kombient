package kombient.movies.tmdb

import feign.Param
import feign.RequestLine

interface TmdbClient {
    @RequestLine("GET /3/search/movie?api_key={apiKey}&language=en-US&query={title}&page=1&include_adult=false")
    fun searchMovieByTitle(
            @Param("title") title: String,
            @Param("apiKey") apiKey: String
    ): TmdbSearchResult

    @RequestLine("GET /3/movie/{id}?api_key={apiKey}")
    fun getMovieById(
            @Param("id") id: Int,
            @Param("apiKey") apiKey: String
    ) : TmdbMovie

    data class TmdbMovie(
            val id: Int,
            val imdb_id: String,
            val popularity: Float,
            val overview: String,
            val title: String,
            val vote_count: Int,
            val vote_average: Float
    )

    data class TmdbSearchResult(
            val page: Int,
            val total_results: Int,
            val total_pages: Int,
            val results: Array<TmdbSearchSummary>
    )

    data class TmdbSearchSummary(
            val vote_count: Int,
            val id: Int,
            val video: Boolean,
            val title: String,
            val popularity: Float,
            val overview: String
    )

}
