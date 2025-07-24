package com.example.infracare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.infracare.R
import com.example.infracare.model.ForumPost

class ForumAdapter(private val list: List<ForumPost>) :
    RecyclerView.Adapter<ForumAdapter.ForumViewHolder>() {

    inner class ForumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvTanggalLokasi: TextView = view.findViewById(R.id.tvTanggalLokasi)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvIsi: TextView = view.findViewById(R.id.tvIsi)
        val imgPost: ImageView = view.findViewById(R.id.imgPost)
        val tvKomentar: TextView = view.findViewById(R.id.tvKomentar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forum, parent, false)
        return ForumViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForumViewHolder, position: Int) {
        val item = list[position]
        holder.tvNama.text = item.nama
        holder.tvTanggalLokasi.text = "${item.tanggal}, ${item.lokasi}"
        holder.tvJudul.text = item.judul
        holder.tvIsi.text = item.isi
        holder.tvKomentar.text = "${item.jumlahKomentar} Komentar"

        if (item.urlGambar.isNullOrEmpty()) {
            holder.imgPost.visibility = View.GONE
        } else {
            holder.imgPost.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.urlGambar)
                .into(holder.imgPost)
        }

        Glide.with(holder.itemView.context)
            .load(R.drawable.pp)
            .into(holder.imgProfile)
    }

    override fun getItemCount(): Int = list.size
}