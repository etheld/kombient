package kombient.movies.moviemetadata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Repository
interface MovieMetaDataRepository : JpaRepository<MovieMetaData, String> {
    @Query("SELECT DISTINCT m.imdbId from MovieMetaData m")
    fun findDistinctImdbId(): Set<String>

    fun findByImdbId(imdbId: String): MovieMetaData?
}
