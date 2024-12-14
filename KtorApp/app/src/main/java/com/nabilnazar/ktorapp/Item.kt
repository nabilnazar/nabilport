package com.nabilnazar.ktorapp

import kotlinx.serialization.Serializable


@Serializable
data class Item(
    val id: Int = 0,
    val name: String,
    val price: Double
)
