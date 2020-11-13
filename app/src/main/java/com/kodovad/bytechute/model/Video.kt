package com.kodovad.bytechute.model

data class Video (
    val title: String,
    val href: String,
    val channel: String?,           //  missing channel when searching
    val imageURL: String?,
    val viewCount: String?,
    val timePublished: String?,
    val duration: String?           //  missing duration when searching
)