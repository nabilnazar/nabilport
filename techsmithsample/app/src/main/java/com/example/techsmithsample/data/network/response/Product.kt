package com.example.techsmithsample.data.network.response

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity("product_tb")
data class Product(
    @PrimaryKey
    val id: Int? = null,
    val price: Double? = null,
    val rating: Double? = null,
    val quantity: Int = 0,
    val shippingInformation: String? = null,
    val thumbnail: String? = null,
    val title: String? = null
) {
    @Ignore
    var availabilityStatus: String? = null
    @Ignore
    var brand: String? = null
    @Ignore
    var category: String? = null
    @Ignore
    var description: String? = null
    @Ignore
    var discountPercentage: Double? = null
    @Ignore
    var images: List<String>? = null
    @Ignore
    var minimumOrderQuantity: Int? = null
    @Ignore
    var returnPolicy: String? = null
    @Ignore
    var sku: String? = null
    @Ignore
    var stock: Int? = null
    @Ignore
    var tags: List<String>? = null
    @Ignore
    var warrantyInformation: String? = null
    @Ignore
    var weight: Int? = null
}