package com.dicoding.freshfind.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.network.HomeResponse
import com.dicoding.freshfind.network.Product
import com.dicoding.freshfind.network.ProductPhoto
import com.dicoding.freshfind.network.ProductWithPhoto
import com.dicoding.freshfind.util.toProduct
import com.dicoding.freshfind.util.toProductPhoto
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val _productList = MutableLiveData<List<ProductWithPhoto>>()
    val productList: LiveData<List<ProductWithPhoto>> get() = _productList


    private val apiService = ApiClient.create()


    fun fetchProducts() {
        viewModelScope.launch {
            try {
                val response: HomeResponse = apiService.getProducts()

                // Log response size for debugging
                val productSize = response.productDatas?.size ?: 0
                val photoSize = response.productPhotos?.size ?: 0
                Log.d("ProductViewModel", "Data received: $productSize products, $photoSize photos")

                // Convert ProductDatasItem to Product
                val products = response.productDatas?.mapNotNull { it?.toProduct() } ?: emptyList()

                // Convert ProductPhotosItem to ProductPhoto
                val photos = response.productPhotos?.mapNotNull { it?.toProductPhoto() } ?: emptyList()

                // Map products to their corresponding photos
                val productsWithPhotos = mapProductsToWithPhotos(products, photos)

                // Update LiveData with mapped data
                _productList.postValue(productsWithPhotos)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products: ${e.message}")
            }
        }
    }


    private fun mapProductsToWithPhotos(
        products: List<Product>,
        productPhotos: List<ProductPhoto>
    ): List<ProductWithPhoto> {
        val photoMap = productPhotos.associateBy { it.productId }
        return products.map { product ->
            ProductWithPhoto(
                product = product,
                photoUrl = photoMap[product.id]?.link
            )
        }
    }

}
