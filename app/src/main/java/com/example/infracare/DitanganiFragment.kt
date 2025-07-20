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

class DitanganiFragment : Fragment() {

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
                id = 4,
                kategori = "Saluran Tersumbat",
                tanggal = "10-07-2025",
                judul = "Saluran air mampet di Jl Soekarno-Hatta RT 01 RW 01",
                lokasi = "Gg. Bu Ilem No.128, Cibaduyut Wetan, Kec. Bojongloa Kidul, Kota Bandung, Jawa Barat 40238, Indonesia",
                status = "Ditangani",
                imageUrl = "https://newskaltim.com/wp-content/uploads/2017/08/Tumpukan-sampah-di-drainase-Gunung-Kawi.-int.jpg"
            )
        )

        laporanAdapter = LaporanAdapter(dummyData) { selectedLaporan ->
            val bundle = Bundle().apply {
                putParcelable("laporan", selectedLaporan)
            }

            val detailFragment = DetailLaporanFragment().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = laporanAdapter
        return view
    }
}
