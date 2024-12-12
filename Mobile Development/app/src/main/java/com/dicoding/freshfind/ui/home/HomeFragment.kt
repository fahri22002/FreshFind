package com.dicoding.freshfind.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.freshfind.databinding.FragmentHomeBinding
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.network.Product
import com.dicoding.freshfind.network.ProductWithPhoto
import com.dicoding.freshfind.network.ProductPhoto
import com.dicoding.freshfind.network.SearchRequest
import com.dicoding.freshfind.ui.ProductAdapter
import com.dicoding.freshfind.ui.adapters.HomePagerAdapter
import com.dicoding.freshfind.ui.addproduct.AddProductActivity
import com.dicoding.freshfind.utils.SessionManager
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_ADD_PRODUCT = 2001
    private val REQUEST_GALLERY = 1001
    private val apiService = ApiClient.create()

    private lateinit var homePagerAdapter: HomePagerAdapter
    private lateinit var productAdapter: ProductAdapter

    private var productPhotos: List<ProductPhoto> = listOf()  // Menyimpan data foto produk

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewPagerAndTabs()
        setupRecyclerView()
        setupSearchBar()
        setupCameraIcon()
        setupAddProductButton()

        return root
    }

    private fun setupAddProductButton() {
        val userRole = SessionManager.getRole(requireContext())
        binding.btnAddProduct.visibility = if (userRole == "Seller") View.VISIBLE else View.GONE

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_PRODUCT)
        }
    }
    private fun setupViewPagerAndTabs() {
        homePagerAdapter = HomePagerAdapter(this)
        binding.viewPager.adapter = homePagerAdapter

        TabLayoutMediator(binding.tablayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Fish"
                1 -> "Fruits"
                2 -> "Veggies"
                else -> null
            }
        }.attach()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter {
            // Handle product click, can add more actions here
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = productAdapter
    }

    private fun setupSearchBar() {
        binding.searchViewHome.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        searchProductsByKeyword(it)
                    } else {
                        Toast.makeText(requireContext(), "Search query is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupCameraIcon() {
        binding.cameraIcon.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        recognizeProductFromImage(it)
                    } ?: Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun recognizeProductFromImage(imageUri: Uri) {
        lifecycleScope.launch {
            try {
                val tempFile = createTempFileFromUri(imageUri)
                val requestBody = tempFile.asRequestBody()
                val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)

                val response = withContext(Dispatchers.IO) {
                    apiService.recognizeProduct(imagePart)
                }

                if (response.isSuccessful) {
                    val result = response.body()
                    if (!result?.data.isNullOrEmpty()) {
                        val recognizedProduct = result?.data?.get(0)
                        recognizedProduct?.let {
                            // Update search bar and directly show the recognized product
                            binding.searchViewHome.setQuery(it.name, true)

                            // Display recognized product in the recycler view
                            updateProductList(listOf(it), it.name)
                        }
                    } else {
                        Toast.makeText(requireContext(), "No products found from the image!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(requireContext(), "Failed to recognize product: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun createTempFileFromUri(uri: Uri): File {
        return withContext(Dispatchers.IO) {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        }
    }

    private fun updateProductList(products: List<Product>, query: String) {
        val productWithPhotoList = products.map { product ->
            // Mencocokkan produk dengan foto berdasarkan id
            val photoUrl = findPhotoUrlForProduct(product.id)
            ProductWithPhoto(product, photoUrl)
        }
        // Perbarui RecyclerView dengan data baru
        productAdapter.submitList(productWithPhotoList)
    }


    private fun findPhotoUrlForProduct(productId: String): String? {
        // Temukan URL foto berdasarkan productId
        val productPhoto = productPhotos.find { it.productId == productId }
        return productPhoto?.link // Kembalikan link foto atau null jika tidak ditemukan
    }

    private fun searchProductsByKeyword(keyword: String) {
        // Tampilkan ProgressBar
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val request = SearchRequest(keyword)
                val response = withContext(Dispatchers.IO) {
                    apiService.searchProduct(request)
                }

                // Sembunyikan ProgressBar setelah pencarian selesai
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    val result = response.body()
                    // Check if data is not null and not empty
                    if (result?.data != null && result.data.isNotEmpty()) {
                        updateProductList(result.data, keyword)
                    } else {
                        Toast.makeText(requireContext(), "No products found for $keyword", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(requireContext(), "Failed to search products: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
