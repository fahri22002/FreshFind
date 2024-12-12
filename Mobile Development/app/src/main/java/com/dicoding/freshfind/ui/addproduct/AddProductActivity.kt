package com.dicoding.freshfind.ui.addproduct

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.freshfind.R
import com.dicoding.freshfind.network.AddProductRequest
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.utils.SessionManager
import kotlinx.coroutines.launch

class AddProductActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        spinnerCategory = findViewById(R.id.spinnerCategory)

        // Setup Spinner
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Tombol Submit
        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val name = findViewById<EditText>(R.id.etProductName).text.toString()
            val price = findViewById<EditText>(R.id.etProductPrice).text.toString()
            val stock = findViewById<EditText>(R.id.etProductStock).text.toString().toIntOrNull()
            val description = findViewById<EditText>(R.id.etProductDescription).text.toString()

            if (name.isBlank() || price.isBlank() || stock == null || description.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi kategori yang dipilih
            val validCategories = listOf("ikan", "buah", "sayur")  // Pastikan kategori valid
            if (!validCategories.contains(selectedCategory)) {
                Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kirim data ke API
            addProductToApi(name, price, stock, selectedCategory, description)
        }
    }

    private fun addProductToApi(name: String, price: String, stock: Int, category: String, description: String) {
        val apiService = ApiClient.create()
        lifecycleScope.launch {
            try {
                val token = SessionManager.getToken(this@AddProductActivity)
                if (token.isNullOrBlank()) {
                    Toast.makeText(this@AddProductActivity, "User is not authenticated", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                Log.d("AddProductActivity", "Token: $token")
                val response = apiService.addProduct(
                    token = token,
                    request = AddProductRequest(
                        name = name,
                        price = price,
                        stock = stock,
                        category = category,
                        description = description
                    )
                )

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val productId = responseBody?.productId ?: "Unknown"
                    Log.d("AddProductActivity", "Product ID: $productId")
                    Toast.makeText(this@AddProductActivity, "Product added successfully! ID: $productId", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // Informasikan keberhasilan
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Failed to add product"
                    Log.e("AddProductActivity", "Error: $errorMessage")
                    Toast.makeText(this@AddProductActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AddProductActivity", "Exception: ${e.message}", e)
                Toast.makeText(this@AddProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
