package kombient.slack.slackbotcommands

import kombient.movies.movieuserrating.MovieUserRatingService
import kombient.movies.tmdb.TmdbService
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class ImdbTvSlackBotCommand(
    private val tmdbService: TmdbService,
    private val movieUserRatingService: MovieUserRatingService,
    @Value("\${imdb.similarityscore}") private val imdbSimilarityScore: Double
) : SlackBotCommand {

    companion object {
        const val command = "!imdbtv"
    }

    private val commandRegex = Regex("$command (.+)")

    override fun isMatched(message: String): MatchResult? {
        return commandRegex.matchEntire(message)
    }

    override fun process(message: String): Optional<String> {
        val match = isMatched(message)
        if (match != null) {
            val (title) = match.destructured

            val findTV = tmdbService.findTV(title)

            if (findTV.results.isNotEmpty()) {
                val scoredResults = findTV.results.map { Pair(it, FuzzySearch.ratio(it.name, title) * it.popularity) }
                val best = scoredResults.maxBy { it.second }!!
                val top90PercentMatches = scoredResults.filter { it.second > best.second * imdbSimilarityScore }

                val prefix = if (top90PercentMatches.size > 1) "Found multiple matches:\n" else ""

                return Optional.of(prefix + top90PercentMatches
                    .map { tmdbService.getTvById(it.first.id) }
                    .joinToString("\n") { tmdbTV ->
                        val imdbId = tmdbService.getImdbIdByTvId(tmdbTV.id).imdb_id
                        String.format("%s https://imdb.com/title/%s %s", tmdbTV, imdbId, movieUserRatingService.getUserRatingsForImdbMovie(imdbId))
                    })
            }
        }
        return Optional.of("Could not find it, maybe you misspelled it or it is a movie? Try !imdb")
    }
}
