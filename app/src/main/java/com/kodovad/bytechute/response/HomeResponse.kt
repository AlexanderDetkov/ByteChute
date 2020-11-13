package com.kodovad.bytechute.response

import com.kodovad.bytechute.model.Channel
import com.kodovad.bytechute.model.Tag
import com.kodovad.bytechute.model.Video

data class HomeResponse (
    val popularVideos: List<Video>,
    val popularChannels: List<Channel>,
    val subscribedVideos: List<Video>,
    val trendingDayVideos: List<Video>,
    val trendingWeekVideos: List<Video>,
    val trendingMonthVideos: List<Video>,
    val trendingTags: List<Tag>,
    val allVideos: List<Video>
)