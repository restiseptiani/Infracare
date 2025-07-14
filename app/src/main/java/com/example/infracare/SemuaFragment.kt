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
        NewsItem(
            title = "Proyek Air Bersih di Pedesaan",
            imageUrl = "https://i.pinimg.com/736x/20/77/82/20778282c48ab25d101fd9baac35f65f.jpg",
            category = "Hidup Sehat",
            tags = listOf("Air Bersih", "Desa"),
            iconResId = R.drawable.ic_health
        ),
        NewsItem(
            title = "Inovasi Sistem Drainase Terbaru",
            imageUrl = "https://i.pinimg.com/1200x/5a/d6/91/5ad691afbeb1a38dacfb43110a31db5d.jpg",
            category = "Infrastruktur",
            tags = listOf("Drainase", "Perkotaan"),
            iconResId = R.drawable.ic_health
        )
    )

    private val newsItems = listOf(
        NewsItem(
            title = "Inovasi Sistem Drainase Anti-Banjir",
            imageUrl = "https://images.unsplash.com/photo-1570129477492-45c003edd2be",
            category = "Drainase",
            source = "Indonesia Emas",
            timestamp = System.currentTimeMillis() - 60 * 60 * 1000 // 1 jam yang lalu
        ),
        NewsItem(
            title = "Pemeliharaan Jalan Raya di Musim Hujan",
            imageUrl = "https://www.suarasurabaya.net/wp-content/uploads/2021/01/WhatsApp-Image-2021-01-18-at-08.44.56-2-840x493.jpeg",
            category = "Transportasi",
            source = "Jabar Infrastruktur",
            timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000 // 2 jam yang lalu
        ),
        NewsItem(
            title = "Revitalisasi Jalur Kereta Api Tua",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRgIAs3gwHNFBZTV5CGzCJyIwAKsWCcoRUwRg&s",
            category = "Publik",
            source = "Kominfo Indonesia",
            timestamp = System.currentTimeMillis() - 3 * 60 * 60 * 1000 // 3 jam yang lalu
            )
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