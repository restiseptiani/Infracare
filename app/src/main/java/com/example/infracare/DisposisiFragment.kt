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
                lokasi = "Gg. Bu Ilem No.128, Cibaduyut Wetan, Kec. Bojongloa Kidul, Kota Bandung, Jawa Barat 40238, Indonesia",
                status = "Disposisi",
                imageUrl = "https://multimedia.beritajakarta.id/photo/2014_508c75c8507a2ae5223dfd2faeb98122/5981b757b0197560271ed10541d2accd.jpg"
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
