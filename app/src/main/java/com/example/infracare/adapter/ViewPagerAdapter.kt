package com.example.infracare.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.infracare.PopulerFragment
import com.example.infracare.SemuaFragment
import com.example.infracare.TerkiniFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SemuaFragment()
            1 -> TerkiniFragment()
            2 -> PopulerFragment()
            else -> SemuaFragment()
        }
    }
}
