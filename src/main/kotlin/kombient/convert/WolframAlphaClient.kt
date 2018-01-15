package kombient.convert

import feign.Param
import feign.RequestLine

data class WolframSubPod(
        val title: String,
        val img: String,
        val plaintext: String
)

data class WolframPod(
        val title: String,
        val scanner: String,
        val id: String,
        val position: Int,
        val error: Boolean,
        val numsubpods: Int,
        val subpods: Array<WolframSubPod>

)

data class WolframQueryResult(
        val success: String,
        val error: String,
        val numpods: Int,
        val pods: Array<WolframPod>
)

data class WolframResult(
        val queryresult: WolframQueryResult
)

interface WolframAlphaClient {
    @RequestLine("GET /v2/query?input={input}&format=image,plaintext&output=JSON&appid=DEMO")
    fun convert(@Param("input") input: String): WolframResult
}
