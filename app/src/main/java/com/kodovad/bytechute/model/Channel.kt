package com.kodovad.bytechute.model

data class Channel (
    val channel: String,
    val href: String,
    val description: String?,       // missing description when home
    val imageURL:String?
)
