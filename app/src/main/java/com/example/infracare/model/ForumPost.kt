package com.example.infracare.model

data class ForumPost(
    var id: String = "",
    val judul: String = "",
    val isi: String = "",
    val tanggal: String = "",
    val lokasi: String = "",
    val urlGambar: String? = null,
    val jumlahKomentar: Int = 0,
    var nama: String? = null, // nanti diisi dari users.namaLengkap
    var fotoProfil: String? = null
)
