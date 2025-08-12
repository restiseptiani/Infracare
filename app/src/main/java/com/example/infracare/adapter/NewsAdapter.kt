package com.example.infracare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.infracare.R
import com.example.infracare.model.NewsItem
import com.google.firebase.Timestamp

class NewsAdapter(private val newsList: List<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageNews)
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val category: TextView = itemView.findViewById(R.id.textCategory)
        val source: TextView = itemView.findViewById(R.id.textSource)
        val time: TextView = itemView.findViewById(R.id.textTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.title.text = news.judul
        holder.category.text = news.kategori
        holder.source.text = news.sumber
        holder.time.text = getTimeAgo(news.timestamp)
        Glide.with(holder.itemView.context).load(news.imageUrl).into(holder.image)
    }


    override fun getItemCount(): Int = newsList.size

    private fun getTimeAgo(timestamp: Timestamp?): String {
        if (timestamp == null) return "Tidak diketahui"

        val timeInMillis = timestamp.toDate().time
        val now = System.currentTimeMillis()
        val diff = now - timeInMillis

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Baru saja"
            minutes < 60 -> "$minutes menit yang lalu"
            hours < 24 -> "$hours jam yang lalu"
            else -> "$days hari yang lalu"
        }
    }

}