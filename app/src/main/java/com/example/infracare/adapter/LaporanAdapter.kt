package com.example.infracare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.infracare.R
import com.example.infracare.model.Laporan

class LaporanAdapter(private val list: List<Laporan>) :
    RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    inner class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKategori: TextView = itemView.findViewById(R.id.tvKategori)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val imgLaporan: ImageView = itemView.findViewById(R.id.imgLaporan)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvLihat: TextView = itemView.findViewById(R.id.tvLihat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.laporan_item, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = list[position]
        holder.tvKategori.text = laporan.kategori
        holder.tvTanggal.text = laporan.tanggal
        holder.imgLaporan.setImageResource(laporan.gambarResId)
        holder.tvJudul.text = laporan.judul
        holder.tvLokasi.text = laporan.lokasi
        holder.tvStatus.text = laporan.status
        holder.tvLihat.text = "Rincian Laporan"
    }

    override fun getItemCount(): Int = list.size
}
