package kombient.convert

import feign.Feign
import feign.Logger
import feign.gson.GsonDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ConvertService {

    @Value("\${wolframAlphaAppId}")
    private lateinit var wolframAlphaAppId: String

    private val client = Feign.builder()
        .decoder(GsonDecoder())
        .logLevel(Logger.Level.FULL)
        .target(kombient.convert.WolframAlphaClient::class.java, "https://api.wolframalpha.com")

    fun convert(input: String): String {
        try {
            val wolframResult = client.convert(input, wolframAlphaAppId)
            if (!wolframResult.queryresult.error) {
                val inputPod = wolframResult.queryresult.pods.first({ it.title == "Input interpretation" || it.title == "Input" })
                val resultPod = wolframResult.queryresult.pods.first({ it.title == "Result" })
                return String.format("%s: %s", inputPod.subpods.first().plaintext, resultPod.subpods.first().plaintext)
            }
            return "Sorry, there was an error on the query"
        } catch (e: Exception) {
            return "Sorry there was a problem with the service"
        }
    }
}
