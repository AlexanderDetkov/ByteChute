package com.kodovad.bytechute.request

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.kodovad.bytechute.model.Video
import com.kodovad.bytechute.response.ExtendResponse
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.nio.charset.Charset

class ExtendRequest(
    private val tabName: String,
    private val category: String?,
    private val offset: Int,
    private val lastID: String,
    private val CSRFToken: String,
    private val listener: Response.Listener<ExtendResponse>,
    errorListener: Response.ErrorListener
) : Request<ExtendResponse>(
    Method.POST,
    if (category != null)
        "https://www.bitchute.com/category/$category/extend/"
    else
        "https://www.bitchute.com/extend/",
    errorListener) {

    override fun getHeaders(): Map<String, String> = mapOf("Referer" to url)


    override fun getParams(): Map<String, String>
        = mapOf("name" to tabName, "offset" to offset.toString(), "last" to lastID, "csrfmiddlewaretoken" to CSRFToken)

    override fun deliverResponse(response: ExtendResponse) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ExtendResponse> {

        //  raw response
        val rawResponse = String(
            response?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

        val jsonResponse = JSONObject(rawResponse)

        return if (jsonResponse.has("success") && jsonResponse.getBoolean("success") && jsonResponse.has("html")){
            val htmlDocument: Document? = Jsoup.parse(jsonResponse.getString("html"))

            val videoList = parseAllPopularSubscribedVideos(htmlDocument)

            Response.success(
                ExtendResponse(videoList,videoList.isEmpty()),
                HttpHeaderParser.parseCacheHeaders(response))
        } else
            Response.error(ParseError())
    }

    private fun parseAllPopularSubscribedVideos(element: Element?): List<Video>{

        val videoCards = element?.getElementsByClass("video-card")

        val videoList = ArrayList<Video>()
        if (videoCards != null)
            for (videoCard in videoCards)
                videoList.add(Video(
                    title = videoCard.getElementsByClass("video-card-title").first().text(),
                    href = videoCard.select("a").attr("href"),
                    imageURL = videoCard.getElementsByClass("img-responsive lazyload").attr("data-src"),
                    channel = videoCard.getElementsByClass("video-card-channel").first().text(),
                    viewCount = videoCard.getElementsByClass("video-views").first().text(),
                    timePublished = videoCard.getElementsByClass("video-card-published").first().text(),
                    duration = videoCard.getElementsByClass("video-duration").first().text()))

        return videoList
    }

}
