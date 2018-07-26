package kombient.movies.tmdb

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "tmdb", url = "https://api.themoviedb.org")
interface TmdbClient {

    @GetMapping("/3/tv/{id}/external_ids")
    fun getImdbIdFromTvId(
        @PathVariable("id") id: Int,
        @RequestParam("api_key") apiKey: String
    ): TmdbExternalIds

    @GetMapping("/3/find/{id}?external_source=imdb_id")
    fun findByImdbId(
        @RequestParam("id") id: String,
        @RequestParam("api_key") apiKey: String
    ): TmdbFindResult

    @GetMapping("/3/search/multi")
    fun findMulti(
        @RequestParam("query") query: String,
        @RequestParam("api_key") apiKey: String
    ): TmdbMultiSearchResult

    @GetMapping("/3/search/movie?language=en-US&page=1&include_adult=false")
    fun findMovieByTitle(
        @RequestParam("query") title: String,
        @RequestParam("api_key") apiKey: String
    ): TmdbMovieSearchResult

    @GetMapping("/3/search/tv?language=en-US&page=1&include_adult=false")
    fun findTVByTitle(
        @RequestParam("query") title: String,
        @RequestParam("api_key") apiKey: String
    ): TmdbTVSearchResult

    @GetMapping("/3/movie/{id}")
    fun getMovieById(
        @PathVariable("id") id: Int,
        @RequestParam("api_key") apiKey: String
    ): TmdbMovie

    @GetMapping("/3/tv/{id}")
    fun getTvById(
        @PathVariable("id") id: Int,
        @RequestParam("api_key") apiKey: String
    ): TmdbTv

    data class TmdbExternalIds(
        val imdb_id: String
    )

    data class TmdbMultiSearchResult(
        val page: Int,
        val total_results: Int,
        val total_pages: Int,
        val results: List<TmdbMultiSearchSummary>
    )

    enum class MediaType {
        TV, MOVIE, PERSON;

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromString(str: String): MediaType {
                return MediaType.values().first { it.name.toLowerCase() == str.toLowerCase() }
            }
        }
    }

    data class TmdbMultiSearchSummary(
        val id: Int = 0,
        val media_type: MediaType = MediaType.MOVIE
    )

    data class TmdbFindMovieResult(
        val id: Int = 0,
        val title: String = ""
    )

    data class TmdbFindTVResult(
        val id: Int = 0,
        val name: String = ""
    )

    data class TmdbFindResult(
        val tv_results: List<TmdbFindTVResult> = ArrayList(),
        val movie_results: List<TmdbFindMovieResult> = ArrayList()
    )

    data class TmdbTv(
        val id: Int,
        val popularity: Float,
        val overview: String,
        val name: String,
        val runtime: Int,
        val first_air_date: String,
        val genres: List<Genre>,
        val vote_count: Int,
        val vote_average: Float
    ) {
        override fun toString(): String {
            return String.format("[IMDb]TV %s(%s) %s/10 from %s votes %s mins [%s]",
                name,
                first_air_date,
                vote_average,
                vote_count,
                runtime,
                genres.joinToString(", ") { it.name })
        }
    }

    data class TmdbMovie(
        val id: Int,
        val imdb_id: String,
        val popularity: Float,
        val overview: String,
        val title: String,
        val runtime: Int,
        val release_date: String,
        val genres: List<Genre>,
        val vote_count: Int,
        val vote_average: Float
    ) {
        override fun toString(): String {
            return String.format("[IMDb] %s(%s) %s mins [%s]",
                title,
                release_date,
                runtime,
                genres.joinToString(", ") { it.name })
        }
    }

    data class Genre(
        val id: Int,
        val name: String
    )

    data class TmdbTVSearchResult(
        val page: Int,
        val total_results: Int,
        val total_pages: Int,
        val results: List<TmdbTVSearchSummary>
    )

    data class TmdbMovieSearchResult(
        val page: Int,
        val total_results: Int,
        val total_pages: Int,
        val results: List<TmdbMovieSearchSummary>
    )

    data class TmdbMovieSearchSummary(
        val vote_count: Int,
        val id: Int,
        val video: Boolean,
        val title: String,
        val popularity: Float,
        val overview: String
    )

    data class TmdbTVSearchSummary(
        val vote_count: Int,
        val id: Int,
        val name: String,
        val popularity: Float,
        val overview: String
    )
}
