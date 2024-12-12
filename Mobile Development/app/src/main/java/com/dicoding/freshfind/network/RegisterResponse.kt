package com.dicoding.freshfind.network

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("address_code")
	val addressCode: Int? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("birthdate")
	val birthdate: String? = null,

	@field:SerializedName("address_province")
	val addressProvince: String? = null,

	@field:SerializedName("address_street")
	val addressStreet: String? = null,

	@field:SerializedName("address_village")
	val addressVillage: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address_subdistrict")
	val addressSubdistrict: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("address_number")
	val addressNumber: Int? = null,

	@field:SerializedName("address_city")
	val addressCity: String? = null
)

data class UserRegisterRequest(
	val number: String,
	val password: String,
	val email: String,
	val name: String,
	val birthdate: String,
	val address_number: Int,
	val address_street: String,
	val address_village: String,
	val address_subdistrict: String,
	val address_city: String,
	val address_province: String,
	val address_code: Int
)

data class SellerRegisterResponse(

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("store_name")
	val storeName: String? = null,

	@field:SerializedName("birthdate")
	val birthdate: String? = null,

	@field:SerializedName("address_number")
	val addressNumber: Int? = null,

	@field:SerializedName("address_street")
	val addressStreet: String? = null,

	@field:SerializedName("address_village")
	val addressVillage: String? = null,

	@field:SerializedName("address_subdistrict")
	val addressSubdistrict: String? = null,

	@field:SerializedName("address_city")
	val addressCity: String? = null,

	@field:SerializedName("address_province")
	val addressProvince: String? = null,

	@field:SerializedName("address_code")
	val addressCode: Int? = null,

	@field:SerializedName("bank_account")
	val bankAccount: String? = null,

	@field:SerializedName("bank_name")
	val bankName: String? = null
)

data class SellerRegisterRequest(
	val number: String,
	val password: String,
	val email: String,
	val store_name: String,
	val birthdate: String,
	val address_number: Int,
	val address_street: String,
	val address_village: String,
	val address_subdistrict: String,
	val address_city: String,
	val address_province: String,
	val address_code: Int,
	val bank_account: String,
	val bank_name: String
)