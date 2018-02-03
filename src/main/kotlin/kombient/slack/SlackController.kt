package kombient.slack

import kombient.convert.ConvertService
import kombient.movies.imdb.ImdbService
import kombient.movies.movieuserrating.MovieUserRatingService
import kombient.movies.tmdb.TmdbService
import kombient.slack.data.SlackEvent
import org.springframework.core.task.TaskExecutor
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class SlackController(
        val imdbService: ImdbService,
        val tmdbService: TmdbService,
        val movieUserRatingService: MovieUserRatingService,
        val slackService: SlackService,
        val convertService: ConvertService,
        val executor: TaskExecutor
) {


    @RequestMapping("/api/events")
    fun event(@RequestBody event: SlackEvent): String {

        val imdbMatch = Regex("!imdb (.+)").matchEntire(event.event.text)
        val imdbLastMatch = Regex("!last(\\d+)? (.+)").matchEntire(event.event.text)
        val convertMatch = Regex("!convert (.+)").matchEntire(event.event.text)


        executor.execute({
            if (imdbLastMatch != null) {
                val (num, title) = imdbLastMatch.destructured
                val message = imdbService.getLastMovieRatingsForUser(num.toIntOrNull() ?: 10, title)
                slackService.sendMessage(event.event.channel, message)
            }
            if (convertMatch != null) {
                val (input) = convertMatch.destructured
                val message = convertService.convert(input)
                slackService.sendMessage(event.event.channel, message)
            }
            if (imdbMatch != null) {
                val (title) = imdbMatch.destructured

                val movieSearchResult = tmdbService.findMovie(title)

                val tmdbMovie = tmdbService.getMovieById(movieSearchResult.results.first().id)
                val imdbMovie = imdbService.getMovieById(tmdbMovie.imdb_id)

                val ratingText = movieUserRatingService.getUserRatingsForImdbMovie(imdbMovie)

                val messageFormat = String.format("[IMDb] %s(%d) %.1f/10 from %s votes %s [%s] http://www.imdb.com/title/%s %s",
                        imdbMovie.Title,
                        imdbMovie.Year,
                        imdbMovie.imdbRating,
                        imdbMovie.imdbVotes.replace(",", ""),
                        imdbMovie.Runtime,
                        imdbMovie.Genre,
                        imdbMovie.imdbID,
                        ratingText)

                slackService.sendMessage(event.event.channel, messageFormat)

            }
        })

        return event.challenge
    }
}
