package com.example.infracare.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.infracare.R
import com.example.infracare.model.NewsItem
import com.library.foysaltech.smarteist.autoimageslider.SliderViewAdapter

class CarouselAdapter(private val items: List<NewsItem>) :
    SliderViewAdapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) : ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.carouselImage)
        val title: TextView = itemView.findViewById(R.id.carouselTitle)
        val chipContainer: ViewGroup = itemView.findViewById(R.id.chipGroup)
        val categoryIcon: ImageView = itemView.findViewById(R.id.category_icon)
        val categoryText: TextView = itemView.findViewById(R.id.category_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CarouselViewHolder, position: Int) {
        val item = items[position]
        val context = viewHolder.itemView.context

        // Set judul dan gambar
        viewHolder.title.text = item.title
        Glide.with(context).load(item.imageUrl).into(viewHolder.image)

        // Set ikon kategori dan teks kategori
        viewHolder.categoryIcon.setImageResource(item.iconResId)
        viewHolder.categoryText.text = item.category

        // Hapus chip sebelumnya agar tidak dobel
        viewHolder.chipContainer.removeAllViews()

        // Tambahkan chip secara dinamis berdasarkan tag
        for ((index, tag) in item.tags.withIndex()) {
            val chip = TextView(context).apply {
                text = tag
                setTextColor(Color.WHITE)
                textSize = 12f
                setPadding(24, 8, 24, 8)

                // Ganti background berdasarkan urutan (index)
                background = ContextCompat.getDrawable(
                    context,
                    if (index == 0) R.drawable.chip_background
                    else R.drawable.chip_background_second
                )

                // Tambahkan margin antar chip
                val params = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 36
                layoutParams = params
            }

            viewHolder.chipContainer.addView(chip)
        }
    }

    override fun getCount(): Int=items.size
}