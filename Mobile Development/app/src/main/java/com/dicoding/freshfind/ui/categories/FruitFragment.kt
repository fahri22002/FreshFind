package com.dicoding.freshfind.ui.categories

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.freshfind.databinding.FragmentCategoryBinding
import com.dicoding.freshfind.ui.ProductAdapter
import com.dicoding.freshfind.ui.ProductDetailActivity
import com.dicoding.freshfind.ui.ProductViewModel

class FruitFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ProductAdapter { productId ->
            // Navigate to ProductDetailActivity with the selected product ID
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter

        // Initialize ViewModel
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        // Observe productList
        productViewModel.productList.observe(viewLifecycleOwner, Observer { products ->
            // Filter products by category "buah"
            val fruitProducts = products.filter { it.product.category == "buah" }
            adapter.submitList(fruitProducts)
        })

        // Fetch products
        productViewModel.fetchProducts()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
