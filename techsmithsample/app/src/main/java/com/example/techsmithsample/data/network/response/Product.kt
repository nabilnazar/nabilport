package com.example.techsmithsample.data.network.response

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity("product_tb")
data class Product(

    @Ignore
    val availabilityStatus: String? = null,
    @Ignore
    val brand: String? = null,
    @Ignore
    val category: String? = null,
    @Ignore
    val description: String? = null,
    @Ignore
    val discountPercentage: Double? = null,
    @PrimaryKey
    val id: Int? = null,
    @Ignore
    val images: List<String>? = null,
    @Ignore
    val minimumOrderQuantity: Int? = null,
    val price: Double? = null,
    val rating: Double? = null,
    val quantity: Int = 0,
    @Ignore
    val returnPolicy: String? = null,

    val shippingInformation: String? = null,
    @Ignore
    val sku: String? = null,
    @Ignore
    val stock: Int? = null,
    @Ignore
    val tags: List<String>? = null,
    val thumbnail: String? = null,
    val title: String? = null,
    @Ignore
    val warrantyInformation: String? = null,
    @Ignore
    val weight: Int? = null
)