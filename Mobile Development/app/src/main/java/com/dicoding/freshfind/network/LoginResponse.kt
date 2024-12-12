package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null
)
data class LoginRequest(
	val number: String,
	val password: String

)