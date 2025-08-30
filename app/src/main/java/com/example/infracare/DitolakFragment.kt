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


class DitolakFragment : Fragment() {

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

        fetchLaporanDitolak()

        return view
    }

    private fun fetchLaporanDitolak() {
        val db = FirebaseFirestore.getInstance()

        // Ambil NIK user dari SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences(
            "UserSession",
            android.content.Context.MODE_PRIVATE
        )
        val nikUser = sharedPref.getString("nik", null)

        if (nikUser != null) {
            db.collection("laporan")
                .whereEqualTo("status", "Ditolak")
                .whereEqualTo("nik", nikUser) // ✅ hanya ambil laporan dari user login
                .get()
                .addOnSuccessListener { documents ->
                    laporanList.clear()
                    for (document in documents) {
                        val laporan = Laporan(
                            id = document.id,
                            deskripsi = document.getString("deskripsi") ?: "",
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

                }
        }
    }
}
