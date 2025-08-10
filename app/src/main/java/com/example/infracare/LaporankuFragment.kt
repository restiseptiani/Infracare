package com.example.infracare

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.infracare.adapter.LaporankuPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LaporankuFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporanku, container, false)

        // Inisialisasi komponen
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        // Atur judul dengan simbol ● berwarna secondary
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val fullText = "●    Laporanku"
        val spannable = SpannableString(fullText)
        val secondaryColor = ContextCompat.getColor(requireContext(), R.color.secondary)
        spannable.setSpan(
            ForegroundColorSpan(secondaryColor),
            0, 1, // hanya karakter ●
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvTitle.text = spannable

        // Atur ViewPager dan TabLayout
        val adapter = LaporankuPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Diterima"
                1 -> "Ditangani"
                2 -> "Disposisi"
                3 -> "Selesai"
                else -> "Ditolak"
            }
        }.attach()

        return view
    }
}
