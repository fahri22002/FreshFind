package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class RecognizeResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: RecognizeData
)

data class RecognizeData(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val productList: List<ProductWithPhoto> // Gunakan model yang sudah ada
)





