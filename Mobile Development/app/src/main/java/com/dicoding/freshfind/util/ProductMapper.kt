package com.dicoding.freshfind.util

import com.dicoding.freshfind.network.Product
import com.dicoding.freshfind.network.ProductDatasItem
import com.dicoding.freshfind.network.ProductPhoto
import com.dicoding.freshfind.network.ProductPhotosItem

fun ProductDatasItem.toProduct(): Product {
    return Product(
        id = this.id ?: "",
        name = this.name ?: "Unknown",
        price = this.price ?: 0,
        category = this.category ?: "Unknown"
    )
}

fun ProductPhotosItem.toProductPhoto(): ProductPhoto {
    return ProductPhoto(
        productId = this.productId ?: "", // Provide a default value for null
        link = this.link ?: "" // Provide a default value for null
    )
}
