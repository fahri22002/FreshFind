package com.dicoding.freshfind.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.freshfind.network.ProductWithPhoto
import com.dicoding.freshfind.ui.categories.FishFragment
import com.dicoding.freshfind.ui.categories.FruitFragment
import com.dicoding.freshfind.ui.categories.VeggieFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FishFragment()
            1 -> FruitFragment()
            2 -> VeggieFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
