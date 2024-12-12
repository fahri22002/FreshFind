package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class AddProductResponse(
    val message: String,
    @SerializedName("product_id") val productId: String
)

