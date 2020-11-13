package com.kodovad.bytechute.request

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.kodovad.bytechute.model.Channel
import com.kodovad.bytechute.model.Tag
import com.kodovad.bytechute.model.Video
import com.kodovad.bytechute.response.HomeResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.nio.charset.Charset

class HomeRequest (
    category: String?,
    private val listener: Response.Listener<HomeResponse>,
    errorListener: Response.ErrorListener
) : Request<HomeResponse>(Request.Method.GET,
    if(category == null)
        "https://www.bitchute.com"
    else
        "https://www.bitchute.com/category/$category/",
    errorListener) {

    override fun deliverResponse(request: HomeResponse) = listener.onResponse(request)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<HomeResponse> {

        //  get raw response
        val rawResponse = String(
            response?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
        )

        //  document from raw response
        val documentResponse: Document? = Jsoup.parse(rawResponse)

        return Response.success(
            HomeResponse(
                popularVideos = parseAllPopularSubscribedVideos(documentResponse?.getElementById("listing-popular")),
                popularChannels = parsePopularChannels(documentResponse?.getElementsByClass("carousel-inner")?.first()),
                subscribedVideos = parseAllPopularSubscribedVideos(documentResponse?.getElementById("listing-subscribed")),
                trendingDayVideos = parseTrendingVideos(documentResponse?.getElementById("trending-day")),
                trendingWeekVideos = parseTrendingVideos(documentResponse?.getElementById("trending-week")),
                trendingMonthVideos = parseTrendingVideos(documentResponse?.getElementById("trending-month")),
                trendingTags = parseTrendingTags(documentResponse?.getElementsByClass("list-inline list-unstyled")?.first()),
                allVideos = parseAllPopularSubscribedVideos(documentResponse?.getElementById("listing-all"))
            ),
            HttpHeaderParser.parseCacheHeaders(response)
        )
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

    private fun parseTrendingVideos(element: Element?): List<Video>{

        val videoCards = element?.getElementsByClass("video-result-container")

        val videoList = ArrayList<Video>()
        if (videoCards != null)
            for (videoCard in videoCards)
                videoList.add(Video(
                    title = videoCard.getElementsByClass("video-result-title").first().text(),
                    href = videoCard.select("a").attr("href"),
                    imageURL = videoCard.getElementsByClass("img-responsive lazyload").attr("data-src"),
                    channel = videoCard.getElementsByClass("video-result-channel").first().text(),
                    viewCount = videoCard.getElementsByClass("video-views").first().text(),
                    timePublished = videoCard.getElementsByClass("video-result-details text-left").first().text(),
                    duration = videoCard.getElementsByClass("video-duration").first().text()))

        return videoList
    }

    private fun parsePopularChannels(channelSectionElement: Element?): List<Channel> {

        return if (channelSectionElement != null){
            val popularChannels = ArrayList<Channel>()

            //  get all channels in list
            val channelElements: Elements? = channelSectionElement.getElementsByClass("spa")

            if (channelElements != null)
                for (channelElement in channelElements)
                    popularChannels.add(Channel(
                            channel = channelElement.text(),
                            href = channelElement.attr("href"),
                            description = null,
                            imageURL = channelElement.getElementsByAttributeValue("alt", "channel image").attr("data-src")))

            popularChannels
        }else
            emptyList()
    }

    private fun parseTrendingTags(element: Element?): List<Tag> {
        val tagElements: Elements? = element?.select("a")

        val tags = ArrayList<Tag>()

        if (tagElements != null)
            for (tagElement in tagElements)
                tags.add(Tag(
                    href = tagElement.attr("href"),
                    title = tagElement.text()))

        return tags
    }}
