package com.dicoding.freshfind.network


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart

import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("home")
    suspend fun getProducts(): HomeResponse

    @POST("products")
    suspend fun getProductDetails(@Body request: ProductRequest): Response<DetailResponse>

    @POST("user/register")
    suspend fun userRegister(@Body request: UserRegisterRequest): Response<UserRegisterResponse>

    @POST("user/login")
    suspend fun userLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("seller/register")
    suspend fun sellerRegister(@Body request: SellerRegisterRequest): Response<SellerRegisterResponse>

    @POST("seller/login")
    suspend fun sellerLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("products/add")
    suspend fun addProduct(@Header("Authorization") token: String, @Body request: AddProductRequest): Response<AddProductResponse>

    @POST("products/search")
    suspend fun searchProduct(@Body request: SearchRequest): Response<SearchResponse>

    @POST("products/recognize")
    @Multipart
    suspend fun recognizeProduct(
        @Part image: MultipartBody.Part
    ): Response<SearchResponse>


}

