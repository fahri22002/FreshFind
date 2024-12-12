package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class HomeResponse(

    @field:SerializedName("product_datas")
    val productDatas: List<ProductDatasItem?>? = null,

    @field:SerializedName("product_photos")
    val productPhotos: List<ProductPhotosItem?>? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class ProductPhotosItem(

    @field:SerializedName("product_id")
    val productId: String? = null,

    @field:SerializedName("link")
    val link: String? = null
)

data class ProductDatasItem(

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("category")
    val category: String? = null
)


