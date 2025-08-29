package com.example.infracare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.infracare.model.Laporan

class DetailLaporanFragment : Fragment() {
    private lateinit var laporan: Laporan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            laporan = it.getParcelable("laporan")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_laporan, container, false)

        view.findViewById<TextView>(R.id.tvJudul).text = laporan.judul
        view.findViewById<TextView>(R.id.tvDeskripsi).text = laporan.deskripsi
        view.findViewById<TextView>(R.id.tvLokasi).text = laporan.lokasi
        view.findViewById<TextView>(R.id.tvStatus).text = laporan.status
        view.findViewById<TextView>(R.id.tvTanggalDibuat).text = laporan.tanggal

        Glide.with(this)
            .load(laporan.imageUrl)
            .into(view.findViewById(R.id.imgLaporan))

        return view
    }
}
