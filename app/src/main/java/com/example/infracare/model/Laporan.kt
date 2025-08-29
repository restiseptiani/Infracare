package com.example.infracare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Laporan(
    var id: String = "",
    val deskripsi: String = "",
    val tanggal: String = "",
    val judul: String = "",
    val lokasi: String = "",
    val status: String = "",
    val imageUrl: String = "",
    val rw: String = "",
    val rt: String = ""
) : Parcelable