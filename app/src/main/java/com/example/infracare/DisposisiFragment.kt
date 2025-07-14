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

class DisposisiFragment : Fragment() {

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
                id = 5,
                kategori = "PJU Rusak",
                tanggal = "12-07-2025",
                judul = "Lampu jalan rusak di depan SD Cibaduyut",
                lokasi = "Jl Cibaduyut Lama, Bandung",
                status = "Disposisi",
                gambarResId = R.drawable.dummy
            )
        )

        laporanAdapter = LaporanAdapter(dummyData)
        recyclerView.adapter = laporanAdapter

        return view
    }
}
