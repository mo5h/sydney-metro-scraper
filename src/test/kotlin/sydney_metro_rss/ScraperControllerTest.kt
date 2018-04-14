package sydney_metro_rss

import org.jsoup.Jsoup
import java.io.File
import kotlin.jvm.javaClass
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains

internal class ScraperControllerTest {

    @org.junit.jupiter.api.Test
    fun getRss() {
        val filename = this.javaClass::class.java.getResource("/sydney-metro-sample.html").file
        val file = File(filename)
        val result = ScraperController().getRss(
                Jsoup.parse(file, null));

        //test that the links come through
        assertThat(result, StringContains.containsString("<a>"))
    }
}