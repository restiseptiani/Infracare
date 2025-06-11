package com.example.infracare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.infracare.adapter.CarouselAdapter
import com.example.infracare.adapter.NewsAdapter
import com.example.infracare.adapter.ViewPagerAdapter
import com.example.infracare.model.NewsItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var carouselSlider: SliderView
    private lateinit var recyclerView: RecyclerView

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var newsAdapter: NewsAdapter

    private val carouselItems = listOf(
        NewsItem("Proyek Air Bersih di Pedesaan", "https://example.com/image1.jpg"),
        NewsItem("Inovasi Sistem Drainase", "https://example.com/image2.jpg")
    )

    private val newsItems = listOf(
        NewsItem("Inovasi Sistem Drainase Anti-Banjir", "https://example.com/drainase.jpg"),
        NewsItem("Pemeliharaan Jalan Raya di Musim Hujan", "https://example.com/jalan.jpg"),
        NewsItem("Revitalisasi Jalur Kereta Api Tua", "https://example.com/kereta.jpg")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        carouselSlider = view.findViewById(R.id.carouselSlider)
        recyclerView = view.findViewById(R.id.newsRecyclerView)

        setupTabs()
        setupCarousel()
        setupRecyclerView()

        return view
    }

    private fun setupTabs() {
        val tabTitles = listOf("Semua", "Terkini", "Populer")
        viewPager.adapter = ViewPagerAdapter(requireActivity())

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupCarousel() {
        carouselAdapter = CarouselAdapter(carouselItems)
        carouselSlider.setSliderAdapter(carouselAdapter)
        carouselSlider.startAutoCycle()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(newsItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = newsAdapter
    }
}
