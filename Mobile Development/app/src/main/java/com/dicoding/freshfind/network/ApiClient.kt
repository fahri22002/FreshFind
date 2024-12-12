package com.dicoding.freshfind.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://app.freshfind.dev/api/"

    fun create(): ApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Waktu koneksi
            .readTimeout(30, TimeUnit.SECONDS)    // Waktu membaca data
            .writeTimeout(30, TimeUnit.SECONDS)   // Waktu menulis data
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Menambahkan OkHttpClient ke Retrofit
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
