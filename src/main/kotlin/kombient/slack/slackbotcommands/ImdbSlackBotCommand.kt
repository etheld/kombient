package kombient.slack.slackbotcommands

import kombient.movies.movieuserrating.MovieUserRatingService
import kombient.movies.tmdb.TmdbService
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbSlackBotCommand(
    private val tmdbService: TmdbService,
    private val movieUserRatingService: MovieUserRatingService,
    @Value("\${imdb.similarityscore}") private val imdbSimilarityScore: Double
) : SlackBotCommand {

    private val commandRegex = Regex("!imdb (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)
        if (match != null) {
            val (title) = match.destructured

            val findMovie = tmdbService.findMovie(title)

            if (findMovie.results.isNotEmpty()) {

                val scoredResults = findMovie.results.map { Pair(it, FuzzySearch.ratio(it.title, title) * it.popularity) }
                val best = scoredResults.maxBy { it.second }!!
                val top90PercentMatches = scoredResults.filter { it.second > best.second * imdbSimilarityScore }

                val prefix = if (top90PercentMatches.size > 1) "Found multiple matches:\n" else ""

                return Optional.of(prefix + top90PercentMatches
                    .map { tmdbService.getMovieById(it.first.id) }
                    .joinToString("\n") { tmdbMovie ->
                        String.format("%s https://imdb.com/title/%s %s", tmdbMovie, tmdbMovie.imdb_id, movieUserRatingService.getUserRatingsForImdbMovie(tmdbMovie.imdb_id))
                    }
                )
            }
        }
        return Optional.of("Could not find it, maybe you misspelled it or it is a tv series? Try !tv")
    }
}
