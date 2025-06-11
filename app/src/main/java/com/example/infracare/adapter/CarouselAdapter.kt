package com.example.infracare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.infracare.R
import com.example.infracare.model.NewsItem

class CarouselAdapter(private val items: List<NewsItem>) : SliderViewAdapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.carouselImage)
        val title: TextView = itemView.findViewById(R.id.carouselTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CarouselViewHolder, position: Int) {
        val item = items[position]
        viewHolder.title.text = item.title
        Glide.with(viewHolder.itemView.context).load(item.imageUrl).into(viewHolder.image)
    }

    override fun getCount(): Int = items.size
}
