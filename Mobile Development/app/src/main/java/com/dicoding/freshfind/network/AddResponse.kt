package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class AddResponse(

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("category")
	val category: String? = null
)
