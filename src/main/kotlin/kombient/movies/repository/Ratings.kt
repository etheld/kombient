package kombient.movies.repository

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "imdb_ratings")
data class Rating(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        val name: String = "",
        @OneToOne(fetch = FetchType.EAGER) @Fetch(FetchMode.JOIN) @JoinColumn(name = "imdb_id") val title: ImdbTitles = ImdbTitles(),
        val vote: Int = 0,
        val date: Date = Date())

@Entity
@Table(name = "imdb_titles")
data class ImdbTitles(
        @Id @Column val imdb_id: String = "",
        val title: String = ""
)

@Repository
interface RatingsRepository : JpaRepository<Rating, Long> {
    @Query("SELECT r FROM Rating r WHERE r.title.imdb_id = :imdb_id")
    fun findByImdbId(@Param("imdb_id") imdb_id: String): List<Rating>

    @Query("SELECT r FROM Rating r INNER JOIN FETCH r.title WHERE r.name = :name order by r.date desc")
    fun findLastVotesForUser(@Param("name") name: String, pageable: Pageable): List<Rating>
}
