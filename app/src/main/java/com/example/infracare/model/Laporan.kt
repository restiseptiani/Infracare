package com.example.infracare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Laporan(
    var id: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val tanggal: String = "",
    val lokasi: String = "",
    val status: String = "",
    val imageUrl: String = "",
    val rt: String = "",
    val rw: String = "",
    val nik: String = "",
    val nama: String = ""
) : Parcelable
