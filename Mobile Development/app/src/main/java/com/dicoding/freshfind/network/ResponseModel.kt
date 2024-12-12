package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductResponse(
    val message: String,

    @SerializedName("product_datas")
    val productDatas: List<Product>,

    @SerializedName("product_photos")
    val productPhotos: List<ProductPhoto>
)

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val category: String
)

data class ProductPhoto(
    @SerializedName("product_id") val productId: String,
    val link: String
)

data class ProductWithPhoto(
    val product: Product,
    val photoUrl: String?
)
