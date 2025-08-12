package com.example.infracare.model

import com.example.infracare.R
import com.google.firebase.Timestamp

data class NewsItem(
    var id: String = "",
    val judul: String = "",
    val imageUrl: String = "",
    val kategori: String = "",
    val tags: List<String> = listOf(),
    val iconResId: Int = R.drawable.ic_health,
    val sumber: String = "",
    val timestamp: Timestamp? = null
)
