package com.dicoding.freshfind.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.freshfind.databinding.ItemProductBinding
import com.dicoding.freshfind.network.ProductWithPhoto

class ProductAdapter(
    private val onProductClick: (String) -> Unit
) : ListAdapter<ProductWithPhoto, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productWithPhoto: ProductWithPhoto) {
            binding.productName.text = productWithPhoto.product.name
            binding.productPrice.text = "Rp ${productWithPhoto.product.price}"
            productWithPhoto.photoUrl?.let {
                Glide.with(binding.productImage.context)
                    .load(it)
                    .into(binding.productImage)
            }

            binding.root.setOnClickListener {
                onProductClick(productWithPhoto.product.id)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductWithPhoto>() {
        override fun areItemsTheSame(oldItem: ProductWithPhoto, newItem: ProductWithPhoto): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: ProductWithPhoto, newItem: ProductWithPhoto): Boolean {
            return oldItem == newItem
        }
    }

    // Tambahkan fungsi ini untuk memperbarui daftar produk
    fun updateProducts(newProducts: List<ProductWithPhoto>) {
        val updatedList = ArrayList(newProducts) // Membuat salinan data
        Log.d("ProductAdapter", "Updating products: ${newProducts.size}")
        submitList(updatedList)
    }

}
