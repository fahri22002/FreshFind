package com.dicoding.freshfind.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.freshfind.R
import com.dicoding.freshfind.databinding.ActivityProductDetailBinding
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.network.ProductRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId.isNullOrEmpty()) {
            Toast.makeText(this, "Product ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProductDetails(productId)
    }

    private fun fetchProductDetails(productId: String) {
        val apiService = ApiClient.create()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = ProductRequest(product_id = productId)
                Log.d("RequestBody", "Request: $request")
                val response = apiService.getProductDetails(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("APIResponse", "Response: $body")

                        val productData = response.body()?.data?.productData?.firstOrNull()
                        val sellerData = response.body()?.data?.sellerData?.firstOrNull()
                        val productPhotos = response.body()?.data?.productPhotos?.firstOrNull()

                        if (productData != null) {
                            binding.productName.text = productData.name ?: "Unknown"
                            binding.productPrice.text = "Rp ${productData.price ?: 0}"
                            binding.productDescription.text = productData.description ?: "No description available"
                            binding.productStock.text = "Stock: ${productData.stock ?: 0}"

                            val imageUrl = productPhotos?.link?.let {
                                if (it.startsWith("http")) it else "https://app.freshfind.dev$it"
                            }

                            Glide.with(this@ProductDetailActivity)
                                .load(imageUrl)
                                .into(binding.productImage)

                            // Display seller information if available
                            sellerData?.let {
                                binding.sellerName.text = it.storeName ?: "Unknown"
                                binding.sellerInfoGroup.visibility = View.VISIBLE
                            } ?: run {
                                binding.sellerInfoGroup.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(this@ProductDetailActivity, "Product not found", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@ProductDetailActivity, "Failed to load product details", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ProductDetailActivity", "Error fetching product details: ${e.message}")
                    Toast.makeText(this@ProductDetailActivity, "Error fetching product details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun showToastAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}
