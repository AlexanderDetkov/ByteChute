package com.kodovad.bytechute.response

data class SearchResponse (
    val searchResults: List<Any>,
    val isFullyExtended: Boolean
)