package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class DetailResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class Data(

    @field:SerializedName("sellerData")
    val sellerData: List<SellerDataItem?>? = null,

    @field:SerializedName("productPhotos")
    val productPhotos: List<ProductPhotosItem?>? = null,

    @field:SerializedName("productData")
    val productData: List<ProductDataItem?>? = null
)

data class ProductDataItem(

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("stock")
    val stock: Int? = null,

    @field:SerializedName("category")
    val category: String? = null,

    @field:SerializedName("seller_id")
    val sellerId: String? = null,

    @field:SerializedName("sold_count")
    val soldCount: Int? = null
)

data class SellerDataItem(

    @field:SerializedName("number")
    val number: String? = null,

    @field:SerializedName("address_province")
    val addressProvince: String? = null,

    @field:SerializedName("photo")
    val photo: String? = null,

    @field:SerializedName("store_name")
    val storeName: String? = null,

    @field:SerializedName("sales_count")
    val salesCount: Int? = null
)


data class ProductRequest(
    val product_id: String
)
