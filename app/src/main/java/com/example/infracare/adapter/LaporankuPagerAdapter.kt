package com.example.infracare.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.infracare.DisposisiFragment
import com.example.infracare.DitanganiFragment
import com.example.infracare.DiterimaFragment
import com.example.infracare.DitolakFragment
import com.example.infracare.SelesaiFragment

class LaporankuPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DiterimaFragment()
            1 -> DitanganiFragment()
            2 -> DisposisiFragment()
            3 -> SelesaiFragment()
            else -> DitolakFragment()
        }
    }
}
