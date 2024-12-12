package com.dicoding.freshfind.network

data class AddProductRequest(
    val name: String,
    val price: String,
    val stock: Int,
    val description: String,
    val category: String
)
