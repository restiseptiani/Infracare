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
import com.example.infracare.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var tvGreeting: TextView
    private lateinit var tvDate: TextView
    private lateinit var ivProfile: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        tvGreeting = view.findViewById(R.id.greetingText)
        tvDate = view.findViewById(R.id.headerDate)
        ivProfile = view.findViewById(R.id.profileImage)

        // ===================== GREETING =====================
        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val namaLengkap = sharedPref.getString("namaLengkap", "User")
        val fotoProfil = sharedPref.getString("fotoProfil", "")

        val calendar = Calendar.getInstance()

        val greeting = "SELAMAT DATANG"

        tvGreeting.text = "$greeting, \n$namaLengkap"

        // ===================== TANGGAL =====================
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(calendar.time)
        tvDate.text = currentDate

        // ===================== FOTO PROFIL =====================
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

        // ===================== HEADER TITLE =====================
        val headerTitle = view.findViewById<TextView>(R.id.headerTitle)
        val fullText = "●    Berita Terkini"
        val spannable = SpannableString(fullText)

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.secondary)),
            0, 1, // hanya karakter ●
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        headerTitle.text = spannable

        setupTabs()
        return view
    }

    private fun setupTabs() {
        val tabTitles = listOf("Semua", "Terkini", "Populer")
        viewPager.adapter = ViewPagerAdapter(requireActivity())

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
