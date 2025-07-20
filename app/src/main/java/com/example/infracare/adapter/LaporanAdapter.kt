package com.example.infracare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.infracare.R
import com.example.infracare.model.Laporan

class LaporanAdapter(
    private val list: List<Laporan>,
    private val onItemClick: (Laporan) -> Unit // ✅ Tambahkan parameter klik
) : RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    inner class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKategori: TextView = itemView.findViewById(R.id.tvKategori)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val image: ImageView = itemView.findViewById(R.id.imgLaporan)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvLihat: TextView = itemView.findViewById(R.id.tvLihat)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(list[position]) // ✅ Panggil callback saat diklik
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.laporan_item, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = list[position]

        holder.tvKategori.text = laporan.kategori
        holder.tvTanggal.text = laporan.tanggal
        Glide.with(holder.itemView.context).load(laporan.imageUrl).into(holder.image)
        holder.tvJudul.text = laporan.judul
        holder.tvLokasi.text = laporan.lokasi
        holder.tvStatus.text = laporan.status
        holder.tvLihat.text = "Rincian Laporan"

        when (laporan.status) {
            "Disposisi" -> holder.tvStatus.setBackgroundResource(R.drawable.chip_background_red)
            "Diterima", "Ditinjau", "Ditangani" -> holder.tvStatus.setBackgroundResource(R.drawable.chip_background)
            else -> holder.tvStatus.setBackgroundResource(R.drawable.chip_background)
        }
    }

    override fun getItemCount(): Int = list.size
}
