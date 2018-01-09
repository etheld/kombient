package kombient.movies.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "imdb_ratings")
data class Rating(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        val name: String = "",
        @Column(name = "imdb_id") val imdbId: String = "",
        val vote: Int = 0,
        val date: Date = Date())


interface RatingsRepository : JpaRepository<Rating, Long> {
    fun findByName(name: String): List<Rating>
    fun findByImdbId(imdb_id: String): List<Rating>
}
