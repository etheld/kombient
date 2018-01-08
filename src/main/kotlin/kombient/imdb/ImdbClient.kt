package kombient.imdb

import feign.Param
import feign.RequestLine
import java.util.*
import javax.ws.rs.PathParam

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

    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ImdbMovie

            if (Title != other.Title) return false
            if (Year != other.Year) return false
            if (Plot != other.Plot) return false
            if (!Arrays.equals(Ratings, other.Ratings)) return false
            if (Type != other.Type) return false

            return true
        }

        override fun hashCode(): Int {
            var result = Title.hashCode()
            result = 31 * result + Year
            result = 31 * result + Plot.hashCode()
            result = 31 * result + Arrays.hashCode(Ratings)
            result = 31 * result + Type.hashCode()
            return result
        }
    }

    data class Rating(val Source: String, val Value: String)
}