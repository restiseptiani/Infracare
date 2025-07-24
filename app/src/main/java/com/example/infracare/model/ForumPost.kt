package com.example.infracare.model

data class ForumPost(
    val id: Int,
    val nama: String,
    val tanggal: String,
    val lokasi: String,
    val judul: String,
    val isi: String,
    val urlGambar: String?,
    val jumlahKomentar: Int
)