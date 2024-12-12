    package com.dicoding.freshfind.ui.categories

    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.dicoding.freshfind.databinding.FragmentCategoryBinding
    import com.dicoding.freshfind.network.ApiClient
    import com.dicoding.freshfind.network.ProductRequest
    import com.dicoding.freshfind.ui.ProductAdapter
    import com.dicoding.freshfind.ui.ProductDetailActivity
    import com.dicoding.freshfind.ui.ProductViewModel
    import kotlinx.coroutines.launch

    class FishFragment : Fragment() {

        private var _binding: FragmentCategoryBinding? = null
        private val binding get() = _binding!!

        private lateinit var productViewModel: ProductViewModel

        private val apiService = ApiClient.create()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentCategoryBinding.inflate(inflater, container, false)

            // Set up RecyclerView
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = ProductAdapter { productId ->
                // Navigate to ProductDetailActivity with the selected product ID
                sendPostRequest(productId)
            }

            binding.recyclerView.adapter = adapter

            // Initialize ViewModel
            productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

            // Observe productList
            productViewModel.productList.observe(viewLifecycleOwner, Observer { products ->
                // Filter products by category "ikan"
                val fishProducts = products.filter { it.product.category == "ikan" }
                adapter.submitList(fishProducts)
            })

            // Fetch products
            productViewModel.fetchProducts()

            return binding.root
        }


        private fun sendPostRequest(productId: String) {
            lifecycleScope.launch {
                try {
                    // Create a ProductRequest object
                    val request = ProductRequest(product_id = productId)

                    // Make the POST request using Retrofit
                    val response = apiService.getProductDetails(request)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Request Successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                        intent.putExtra("PRODUCT_ID", productId) // Pass productId to the next activity
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
