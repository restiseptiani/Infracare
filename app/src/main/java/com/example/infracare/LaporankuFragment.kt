package com.example.infracare

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.infracare.adapter.LaporankuPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LaporankuFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var ivProfile: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporanku, container, false)

        // Inisialisasi komponen
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        ivProfile = view.findViewById(R.id.profileImage)

        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val fotoProfil = sharedPref.getString("fotoProfil", "")

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

        if (!fotoProfil.isNullOrEmpty()) {
            android.util.Log.d("HomeFragment", "Foto profil dari SharedPref: $fotoProfil")
            Glide.with(requireContext())
                .load(fotoProfil)
                .placeholder(R.drawable.pp)
                .error(R.drawable.pp)
                .into(ivProfile)
        } else {
            ivProfile.setImageResource(R.drawable.pp)
        }
        // Atur ViewPager dan TabLayout
        val adapter = LaporankuPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Menunggu Konfirmasi"
                1 -> "Ditangani"
                2 -> "Disposisi"
                3 -> "Selesai"
                else -> "Ditolak"
            }
        }.attach()

        return view
    }
}
