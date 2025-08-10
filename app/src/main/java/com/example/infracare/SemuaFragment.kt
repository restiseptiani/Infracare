package com.example.infracare

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.infracare.adapter.CarouselAdapter
import com.example.infracare.adapter.NewsAdapter
import com.example.infracare.model.NewsItem
import com.google.firebase.firestore.FirebaseFirestore
import com.library.foysaltech.smarteist.autoimageslider.SliderView

class SemuaFragment : Fragment() {

    private lateinit var carouselSlider: SliderView
    private lateinit var recyclerView: RecyclerView

    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var newsAdapter: NewsAdapter

    private val firestore = FirebaseFirestore.getInstance()

    private val carouselItems = mutableListOf<NewsItem>()
    private val newsItems = mutableListOf<NewsItem>()

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

        carouselSlider.apply {
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            scrollTimeInSec = 3
            isAutoCycle = true
            startAutoCycle()
        }

        // Ambil 3 berita terbaru untuk carousel
        firestore.collection("berita")
            .orderBy("timestamp")
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                carouselItems.clear()
                for (document in result) {
                    val item = document.toObject(NewsItem::class.java).copy(
                        id = document.id,
                        iconResId = getIconResIdFromCategory(document.getString("category"))
                    )
                    carouselItems.add(item)
                }
                carouselAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal mengambil data carousel: ", e)
            }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(newsItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = newsAdapter

        val spacingInPx = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.bottom = spacingInPx
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = spacingInPx
                }
            }
        })

        // Ambil semua data berita
        firestore.collection("berita")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                newsItems.clear()
                for (document in result) {
                    val item = document.toObject(NewsItem::class.java).copy(
                        id = document.id,
                        iconResId = getIconResIdFromCategory(document.getString("category"))
                    )
                    newsItems.add(item)
                }
                newsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Gagal mengambil data berita: ", e)
            }
    }

    // Fungsi mapping kategori ke icon
    private fun getIconResIdFromCategory(category: String?): Int {
        return when (category?.lowercase()) {
            "cuaca" -> R.drawable.ic_weather
            "transportasi" -> R.drawable.ic_transportation
            "publik" -> R.drawable.ic_public
            "infrastruktur" -> R.drawable.ic_infrastructure
            else -> R.drawable.ic_health
        }
    }
}
