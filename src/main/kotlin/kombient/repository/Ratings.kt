package kombient.repository

import org.springframework.data.repository.CrudRepository
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


interface RatingsRepository : CrudRepository<Rating, Long> {
    fun findByName(name: String): List<Rating>
    fun findByImdbId(imdb_id: String): List<Rating>
}