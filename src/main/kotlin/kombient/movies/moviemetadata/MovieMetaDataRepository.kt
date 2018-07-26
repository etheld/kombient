package kombient.movies.moviemetadata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 *
 * @author Peter Varsanyi (pevarsanyi@expedia.com)
 */
@Repository
interface MovieMetaDataRepository : JpaRepository<MovieMetaData, String> {
    @Query("SELECT DISTINCT m.imdbId from MovieMetaData m")
    fun findDistinctImdbId(): Set<String>

    @Query("SELECT m from MovieMetaData m join Rating r on r.imdbId = m.imdbId where LOWER(name) = LOWER(:name)")
    fun findAllMetaDataByVotersName(@Param("name") name: String): List<MovieMetaData>

    fun findByImdbId(imdbId: String): MovieMetaData?
}
