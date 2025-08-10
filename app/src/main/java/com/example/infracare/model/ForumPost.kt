package com.example.infracare.model

data class ForumPost(
    var id: String = "",
    val nama: String = "",
    val tanggal: String = "",
    val lokasi: String = "",
    val judul: String = "",
    val isi: String = "",
    val urlGambar: String? = null,
    val jumlahKomentar: Int = 0
)
