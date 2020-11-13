package com.kodovad.bytechute.request

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.kodovad.bytechute.model.Channel
import com.kodovad.bytechute.model.Video
import com.kodovad.bytechute.response.SearchResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.nio.charset.Charset

class SearchRequest(
    query: String, pageNumber: Int, filterBy: String?,
    private val listener: Response.Listener<SearchResponse>,
    errorListener: Response.ErrorListener
) : Request<SearchResponse>(
    Method.GET,
    if(filterBy.isNullOrEmpty())
        "https://search.bitchute.com/renderer?query=$query&page=$pageNumber"
    else
        "https://search.bitchute.com/renderer?query=$query&fqa.kind=$filterBy&page=$pageNumber",
    errorListener) {

    override fun deliverResponse(response: SearchResponse) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<SearchResponse> {

        //  raw html of search page
        val rawHtml = String(
            response?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))


        //  create sections for search items
        val htmlSections = rawHtml.split("<br />")

        //  check number of results
        var isFullyExtended = false
        val numberOfResults = htmlSections.size - 2
        //  less than 0 results
        if (numberOfResults < 0)
            return Response.error(ParseError())
        //  empty or last page
        else if(numberOfResults in 0..9)
            isFullyExtended = true

        //  create content sections
        val contentSections = htmlSections.slice(1..numberOfResults)

        Log.d("here", "Results : $numberOfResults")

        //  parse content
        val contentList = ArrayList<Any>()
        for (contentSection in contentSections){
            contentList.add(parseContentSection(contentSection))
        }

        return Response.success(
            SearchResponse(contentList, isFullyExtended),
            HttpHeaderParser.parseCacheHeaders(response))
    }

    //  content parser
    private fun parseContentSection(contentSection: String): Any{
        val contentDocument = Jsoup.parse(contentSection)

        //  get elements
        val titleElements: Element? = contentDocument.getElementsByClass("osscmnrdr ossfieldrdr1").first()
        val imageElements: Element? = contentDocument.getElementsByClass("osscmnrdr ossfieldrdr2").first()
        val descriptionElements: Element? = contentDocument.getElementsByClass("osscmnrdr ossfieldrdr3").first()
        val kindElements: Element? = contentDocument.getElementsByClass("osscmnrdr ossfieldrdr5 oss-item-kind").first()

        //  create object
        return when (kindElements?.text()) {
            "video" -> {
                val timeElements: Element? = contentDocument.getElementsByClass("osscmnrdr ossfieldrdr7 oss-item-displaydate").first()
                val viewsElements: Element? = contentDocument.getElementsByClass("osscmnrdr ossfieldrdr8 oss-item-displayviews").first()

                Video(
                        title = titleElements?.text() ?: "",
                        href = titleElements?.select("a")?.first()?.attr("abs:href") ?: "",
                        imageURL = imageElements?.select("a")?.select("img")?.attr("data-cfsrc"),
                        channel = null,
                        viewCount = viewsElements?.text(),
                        timePublished = timeElements?.text(),
                        duration = null
                )
            }
            "channel" -> {

                Channel(
                    channel = titleElements?.text() ?: "",
                    description = descriptionElements?.text(),
                    href = titleElements?.select("a")?.first()?.attr("abs:href") ?: "",
                    imageURL = imageElements?.select("a")?.select("img")?.attr("data-cfsrc")
                )
            }
            else -> TODO("Not Video or Channel")
        }
    }
}
