package kombient.movies.imdb

import feign.Param
import feign.RequestLine

interface ImdbClient {
    @RequestLine("GET /?apikey={apiKey}&i={imdbId}")
    fun getMovieById(
            @Param("imdbId") imdbId: String,
            @Param("apiKey") apiKey: String
    ): ImdbMovie

    data class ImdbMovie(
            val Title: String,
            val Year: Int,
            val Plot: String,
            val Ratings: Array<Rating>,
            val imdbRating: Float,
            val imdbVotes: String,
            val Type: String,
            val Runtime: String,
            val Genre: String,
            val imdbID: String
    )

    data class Rating(val Source: String, val Value: String)
}
