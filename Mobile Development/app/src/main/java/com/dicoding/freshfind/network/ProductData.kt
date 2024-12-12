package com.dicoding.freshfind.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductData(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String // Tambahkan kategori untuk produk
) : Parcelable
