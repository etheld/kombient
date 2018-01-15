package kombient.convert

import feign.Feign
import feign.gson.GsonDecoder
import org.springframework.stereotype.Component

@Component
class ConvertService {
    private val client = Feign.builder()
            .decoder(GsonDecoder())
            .target(kombient.convert.WolframAlphaClient::class.java, "https://api.wolframalpha.com")

    fun convert(input: String) {
        client.convert(input)
    }
}
