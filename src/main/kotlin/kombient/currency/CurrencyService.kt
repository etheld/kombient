package kombient.currency

import kombient.movies.parser.ImdbParser
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class CurrencyService {
    fun convert(from: String, to: String, amount: String): String {
        val get = Jsoup
            .connect("https://www.xe.com/currencyconverter/convert/?Amount=$amount&From=$from&To=$to")
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.167 Safari/537.36")
            .maxBodySize(ImdbParser.MAX_BODY_SIZE_30M)
            .get()

        val fromAmount = get.select("span.uccFromResultAmount span.amount").text()
        val toAmount = get.select("span.uccResultAmount").text()
        val toCurrency = get.select("span.uccToCurrencyCode").text()
        return "$fromAmount $from = $toAmount $toCurrency"
    }
}
