package kombient.movies.imdb

import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "omdb", url = "https://www.omdbapi.com/")
interface ImdbClient {
    @GetMapping("/")
    fun getMovieById(
            @RequestParam("i") imdbId: String,
            @RequestParam("apikey") apiKey: String
    ): ImdbMovie

    data class ImdbMovie(
            val Title: String,
            val Year: Int,
            val Plot: String,
            val Ratings: Array<Rating>,
            val imdbRating: String,
            val imdbVotes: String,
            val Type: String,
            val Runtime: String,
            val Genre: String,
            val imdbID: String
    ) {
        override fun toString(): String {
            return String.format("[IMDb] %s(%d) %s/10 from %s votes %s [%s] http://www.imdb.com/title/%s",
                    Title,
                    Year,
                    imdbRating,
                    imdbVotes.replace(",", ""),
                    Runtime,
                    Genre,
                    imdbID)
        }
    }

    data class Rating(val Source: String, val Value: String)
}
