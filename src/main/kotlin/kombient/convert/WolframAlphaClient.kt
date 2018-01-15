package kombient.convert

import feign.Param
import feign.RequestLine

data class WolframImg(
        val src: String = "",
        val alt: String = "",
        val title: String = "",
        val width: Int = 0,
        val height: Int = 0
)

data class WolframSubPod(
        val title: String,
        val img: WolframImg,
        val plaintext: String
)

data class WolframPod(
        val title: String = "",
        val scanner: String = "",
        val id: String = "",
        val position: Int = 0,
        val error: Boolean = false,
        val numsubpods: Int = 0,
        val subpods: Array<WolframSubPod> = emptyArray()

)

data class WolframQueryResult(
        val success: String = "",
        val error: Boolean = false,
        val numpods: Int = 0,
        val pods: Array<WolframPod> = emptyArray()
)

data class WolframResult(
        val queryresult: WolframQueryResult = WolframQueryResult()
)

interface WolframAlphaClient {
    @RequestLine("GET /v2/query?input={input}&format=image,plaintext&output=JSON&appid={appId}")
    fun convert(@Param("input") input: String, @Param("appId") appId: String): WolframResult

}
