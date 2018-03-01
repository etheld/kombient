package kombient.movies.tmdb

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "tmdb", url = "https://api.themoviedb.org")
interface TmdbClient {

    @GetMapping("/3/find/{id}?external_source=imdb_id")
    fun findByImdbId(
            @RequestParam("id") id: String,
            @RequestParam("api_key") apiKey: String
    ): TmdbFindResult

    @GetMapping("/3/search/movie?language=en-US&page=1&include_adult=false")
    fun searchMovieByTitle(
            @RequestParam("query") title: String,
            @RequestParam("api_key") apiKey: String
    ): TmdbSearchResult

    @GetMapping("/3/search/tv?language=en-US&page=1&include_adult=false")
    fun searchTVSeriesByTitle(
            @RequestParam("query") title: String,
            @RequestParam("api_key") apiKey: String
    ): TmdbSearchResult


    @GetMapping("/3/movie/{id}")
    fun getMovieById(
            @PathVariable("id") id: Int,
            @RequestParam("api_key") apiKey: String
    ): TmdbMovie

    @GetMapping("/3/tv/{id}")
    fun getTVById(
            @PathVariable("id") id: Int,
            @RequestParam("api_key") apiKey: String
    ): TmdbMovie

    data class TmdbFindMovieResult(
            val id: Int = 0,
            val title: String = ""
    )

    data class TmdbFindResult(
            val movie_results: ArrayList<TmdbFindMovieResult> = ArrayList()
    )

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
