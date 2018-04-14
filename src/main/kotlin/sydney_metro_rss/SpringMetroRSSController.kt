package sydney_metro_rss


import com.rometools.rome.feed.rss.Content.HTML
import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedOutput
import org.jetbrains.annotations.Nullable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView.DEFAULT_FEED_TYPE
import java.io.File
import java.io.StringWriter
import java.net.URI
import java.text.SimpleDateFormat

const val FEED_TYPE =  DEFAULT_FEED_TYPE
const val SYDNEY_METRO_URL = "https://www.sydneymetro.info/documents"

@RestController
class ScraperController {


    @ResponseBody
    @GetMapping(value= ["/feed.xml"], produces=["application/xml"])

    fun home(): String {
        return getRss(getDoc());
    }

    fun getDoc(): Document{
        return  Jsoup.connect(SYDNEY_METRO_URL).get();
    }

    fun getRss( document : Document) : String {
        val pane = document.getElementByClass("document-list")
        val documents = pane.getElementsByClass("document-list-item")
        val feedElements = documents.map(fun(document: Element): SyndEntry{
            val elements = document.getElementByClass("document-list-item-wrap")
            val item = elements.getElementByClass("document-item-wrap")
            val contentWrap = item.getElementByClass("doc-content-wrap")
            val date = item.getElementByClass("doc-meta").getElementByTag("time").attr("datetime")
            var content = contentWrap.getElementByClass("doc-content").getElementByTag("h2")
            val downloadLink =  contentWrap.getElementByClass("doc-download").getElementByTag("a")
            val entry = SyndEntryImpl()
            entry.setPublishedDate(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(date))

            val syndContentImpl = SyndContentImpl()
            syndContentImpl.value = downloadLink.outerHtml() //needed because we want to copy it as an html element rather than just text
            entry.contents = listOf(syndContentImpl)
            entry.author = "Sydney Metro"
            entry.title = content.html()
            return entry
        })

        val feed = SyndFeedImpl(null, true)

        feed.setTitle("Sydney metro documents");
        feed.setLink("https://www.sydneymetro.info/documents")

        feed.feedType = "atom_1.0";
        feed.entries = feedElements
        feed.description = "RSS feed scraped from Sydney metro's documents library"
        val output = SyndFeedOutput()
        val writer = StringWriter();
        output.output(feed, writer, true);
        return writer.toString()
    }

    fun Element.getElementByClass(string: String): Element{
        return this.getElementsByClass(string).get(0);
    }
    fun Element.getElementByTag(string: String): Element{
        return this.getElementsByTag(string).get(0);
    }

    //data class Document(val date: String, val document: URI)
}