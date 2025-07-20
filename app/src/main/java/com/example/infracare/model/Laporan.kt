package com.example.infracare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Laporan(
    val id: Int,
    val kategori: String,
    val tanggal: String,
    val judul: String,
    val lokasi: String,
    val status: String,
    val imageUrl: String
) : Parcelable
