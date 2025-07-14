package com.example.infracare.model

import com.example.infracare.R

data class NewsItem(
    val title: String,
    val imageUrl: String,
    val category: String,
    val tags: List<String> = listOf(),
    val iconResId: Int = R.drawable.ic_health,
    val source: String = "",
    val timestamp: Long = System.currentTimeMillis() // default: waktu saat ini
)