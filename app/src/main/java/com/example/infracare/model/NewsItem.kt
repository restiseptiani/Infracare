package com.example.infracare.model

import com.example.infracare.R
import com.google.firebase.Timestamp

data class NewsItem(
    var id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val tags: List<String> = listOf(),
    val iconResId: Int = R.drawable.ic_health,
    val source: String = "",
    val timestamp: Timestamp? = null
)
