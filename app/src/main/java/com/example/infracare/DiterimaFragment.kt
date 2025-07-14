package com.example.infracare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.infracare.adapter.LaporanAdapter
import com.example.infracare.model.Laporan


class DiterimaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var laporanAdapter: LaporanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerDiterima)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dummyData = listOf(
            Laporan(
                id = 1,
                kategori = "Jalan Berlubang",
                tanggal = "29-05-2025",
                judul = "Jalan Berlubang di Jl Cibaduyut RT 07 RW 02",
                lokasi = "Jl Raya Cibaduyut, Kota Bandung",
                status = "Diterima",
                gambarResId = R.drawable.dummy // ganti dengan drawable sendiri
            ),
            Laporan(
                id = 2,
                kategori = "Lampu Jalan Mati",
                tanggal = "22-06-2025",
                judul = "Lampu Jalan Mati di Jl Cibaduyut RT 02 RW 08",
                lokasi = "Jl Cibaduyut Kidul, Kota Bandung",
                status = "Diterima",
                gambarResId = R.drawable.dummy
            )
        )

        laporanAdapter = LaporanAdapter(dummyData)
        recyclerView.adapter = laporanAdapter

        return view
    }
}
