package kombient.movies.moviemetadata

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */

@Entity
@Table(name = "movie_metadata")
data class MovieMetaData(
    @Id
    val imdbId: String,
    val title: String,
    val imdbRating: Float?,
    val imdbVotes: Int?,
    val lastUpdated: Instant,
    val runTime: Long
)
