package com.kodovad.bytechute.response

import com.kodovad.bytechute.model.Video

data class ExtendResponse(
    val videos: List<Video>,
    val isFullyExtended: Boolean
)