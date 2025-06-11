package com.example.infracare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.infracare.adapter.CarouselAdapter
import com.example.infracare.adapter.NewsAdapter
import com.example.infracare.model.NewsItem
import com.library.foysaltech.smarteist.autoimageslider.SliderView

class SemuaFragment : Fragment() {

    private lateinit var carouselSlider: SliderView
    private lateinit var recyclerView: RecyclerView

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var newsAdapter: NewsAdapter

    // Dummy data
    private val carouselItems = listOf(
        NewsItem("Proyek Air Bersih di Pedesaan", "https://images.unsplash.com/photo-1503220317375-aaad61436b1b","infrastruktur"),
        NewsItem("Inovasi Sistem Drainase", "https://artaprecast.com/wp-content/uploads/2024/10/Keunggulan-Beton-Precast-Drainase-Perkotaan-3.jpg","infrastruktur")
    )

    private val newsItems = listOf(
        NewsItem("Inovasi Sistem Drainase Anti-Banjir", "https://images.unsplash.com/photo-1570129477492-45c003edd2be", "Infrastruktur"),
        NewsItem("Pemeliharaan Jalan Raya di Musim Hujan", "https://www.suarasurabaya.net/wp-content/uploads/2021/01/WhatsApp-Image-2021-01-18-at-08.44.56-2-840x493.jpeg", "Transportasi"),
        NewsItem("Revitalisasi Jalur Kereta Api Tua", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgIAs3gwHNFBZTV5CGzCJyIwAKsWCcoRUwRg&s", "Publik")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_semua, container, false)

        carouselSlider = view.findViewById(R.id.carouselSlider)
        recyclerView = view.findViewById(R.id.newsRecyclerView)

        setupCarousel()
        setupRecyclerView()

        return view
    }

    private fun setupCarousel() {
        carouselAdapter = CarouselAdapter(carouselItems)
        carouselSlider.setSliderAdapter(carouselAdapter)

        // Konfigurasi tambahan agar slider bergerak otomatis
        carouselSlider.apply {
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            scrollTimeInSec = 3
            isAutoCycle = true
            startAutoCycle()
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(newsItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = newsAdapter
    }
}
