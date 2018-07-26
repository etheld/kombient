package kombient.movies.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "imdb_ratings")
data class Rating(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    val name: String = "",
    @Column(name = "imdb_id", nullable = false) val imdbId: String = "",
    val vote: Int = 0,
    val date: LocalDate = LocalDate.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rating

        if (name != other.name) return false
        if (imdbId != other.imdbId) return false
        if (vote != other.vote) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + imdbId.hashCode()
        result = 31 * result + vote
        result = 31 * result + date.hashCode()
        return result
    }
}

@Repository
interface RatingsRepository : JpaRepository<Rating, Long> {

    @Query("SELECT DISTINCT r.imdbId from Rating r")
    fun findDistinctImdbId(): Set<String>

    fun findByImdbId(imdb_id: String): List<Rating>

    fun findAllByNameIgnoreCaseOrderByDateDesc(name: String, pageable: Pageable): List<Rating>

    fun findAllByNameAndImdbIdIn(name: String, imdbId: List<String>): List<Rating>

    @Query("select r.name as name,count(r.vote) as votes from Rating r group by r.name order by votes desc")
    fun findTopXRaters(pageable: Pageable): List<Map<String, String>>
}
