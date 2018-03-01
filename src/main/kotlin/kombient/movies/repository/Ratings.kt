package kombient.movies.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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
        val date: LocalDate = LocalDate.now())

@Entity
@Table(name = "imdb_titles")
data class ImdbTitle(
        @Id @Column(name = "imdb_id") val imdbId: String = "",
        val title: String = ""
)

@Repository
interface ImdbTitleRepository : JpaRepository<ImdbTitle, Long> {
    fun findByImdbId(imdb_id: String): ImdbTitle
}

@Repository
interface RatingsRepository : JpaRepository<Rating, Long> {

    fun findByImdbId(imdb_id: String): List<Rating>

    @Query("SELECT r FROM Rating r WHERE lower(r.name) = lower(:name) order by r.date desc")
    fun findLastVotesForUser(@Param("name") name: String, pageable: Pageable): List<Rating>

    fun findAllByName(name: String): List<Rating>

}
