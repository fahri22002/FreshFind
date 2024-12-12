//package com.dicoding.freshfind.ui
//
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.dicoding.freshfind.databinding.ActivityHomeSearchBinding
//import com.dicoding.freshfind.network.ApiClient
//import com.dicoding.freshfind.network.ProductWithPhoto
//import kotlinx.coroutines.launch
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import java.io.File
//
//class HomeSearchActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityHomeSearchBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityHomeSearchBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val imagePath = intent.getStringExtra("image_path")
//        if (imagePath != null) {
//            val imageUri = Uri.parse(imagePath)
//            val imgFile = getFileFromUri(imageUri)
//            if (imgFile != null && imgFile.exists()) {
//                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//                binding.capturedImageView.setImageBitmap(bitmap)
//            } else {
//                showError("Image file not found at path: $imagePath")
//            }
//        } else {
//            showError("Image path not received from intent")
//        }
//
//        binding.analyzeButton.setOnClickListener {
//            val imagePath = intent.getStringExtra("image_path")
//            if (imagePath != null) {
//                val imageUri = Uri.parse(imagePath)
//                val imgFile = getFileFromUri(imageUri)
//                if (imgFile != null && imgFile.exists()) {
//                    analyzeImage(imgFile)
//                } else {
//                    showError("Image file not found for analysis")
//                }
//            } else {
//                showError("Image path not received for analysis")
//            }
//        }
//    }
//
//    private fun analyzeImage(file: File) {
//        Log.d("HomeSearchActivity", "Sending file to API: ${file.name}")
//        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
//
//        val apiService = ApiClient.create()
//        lifecycleScope.launch {
//            try {
//                val response = apiService.recognizeProduct(multipartBody)
//                if (response.isSuccessful) {
//                    val recognizeResponse = response.body()
//                    val productList = recognizeResponse?.data?.productList.orEmpty()
//
//                    if (productList.isNotEmpty()) {
//                        displayProducts(productList)
//                        Log.d("HomeSearchActivity", "Products found: ${productList.size}")
//                    } else {
//                        showError("No products found for the given image")
//                        Log.d("HomeSearchActivity", "Empty product list in API response")
//                    }
//                } else {
//                    showError("Failed to analyze image: ${response.message()} (Code: ${response.code()})")
//                    Log.e("HomeSearchActivity", "API call failed: ${response.code()} ${response.message()}")
//                }
//            } catch (e: Exception) {
//                showError("Error during API call: ${e.message}")
//                Log.e("HomeSearchActivity", "Exception during API call", e)
//            }
//        }
//    }
//
//    private fun displayProducts(productList: List<ProductWithPhoto>) {
//        Log.d("HomeSearchActivity", "Displaying products in RecyclerView")
//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        val adapter = ProductAdapter { productId ->
//            Toast.makeText(this, "Product ID: $productId", Toast.LENGTH_SHORT).show()
//        }
//        binding.recyclerView.adapter = adapter
//        adapter.submitList(productList)
//    }
//
//    private fun getFileFromUri(uri: Uri): File? {
//        return try {
//            val contentResolver = contentResolver
//            val fileName = uri.lastPathSegment ?: "temp_image.jpg"
//            val file = File(cacheDir, fileName)
//            contentResolver.openInputStream(uri)?.use { inputStream ->
//                file.outputStream().use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//            file
//        } catch (e: Exception) {
//            Log.e("HomeSearchActivity", "Error creating file from URI", e)
//            null
//        }
//    }
//
//    private fun showError(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        Log.e("HomeSearchActivity", message)
//    }
//}
