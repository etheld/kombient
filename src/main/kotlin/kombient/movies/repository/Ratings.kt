package kombient.movies.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
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

@Entity
@Table(name = "imdb_titles")
data class ImdbTitle(
        @Id @Column(name = "imdb_id") val imdbId: String = "",
        val title: String = ""
)

@Repository
interface RatingsRepository : JpaRepository<Rating, Long> {

    fun findByImdbId(imdb_id: String): List<Rating>

    fun findAllByNameIgnoreCaseOrderByCreatedDesc(name: String, pageable: Pageable): List<Rating>

    fun findAllByNameAndImdbIdIn(name: String, imdbId: List<String>): List<Rating>

}
