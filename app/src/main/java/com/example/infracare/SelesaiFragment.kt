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
import com.google.firebase.firestore.FirebaseFirestore

class SelesaiFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var laporanAdapter: LaporanAdapter
    private val laporanList = mutableListOf<Laporan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerDiterima)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        laporanAdapter = LaporanAdapter(laporanList) { selectedLaporan ->
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

        fetchLaporanSelesai()

        return view
    }

    private fun fetchLaporanSelesai() {
        val db = FirebaseFirestore.getInstance()
        db.collection("laporan")
            .whereEqualTo("status", "Selesai")
            .get()
            .addOnSuccessListener { documents ->
                laporanList.clear()
                for (document in documents) {
                    val laporan = Laporan(
                        id = document.id,
                        kategori = document.getString("kategori") ?: "",
                        tanggal = document.getString("tanggal") ?: "",
                        judul = document.getString("judul") ?: "",
                        lokasi = document.getString("lokasi") ?: "",
                        status = document.getString("status") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                    laporanList.add(laporan)
                }
                laporanAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error, misalnya log atau tampilkan Toast
            }
    }
}
