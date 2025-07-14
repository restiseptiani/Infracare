package com.example.infracare.model

data class Laporan(
    val id: Int,
    val kategori: String,
    val tanggal: String,
    val judul: String,
    val lokasi: String,
    val status: String,
    val gambarResId: Int
)
