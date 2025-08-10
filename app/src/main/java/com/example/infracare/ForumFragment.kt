package com.example.infracare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.infracare.adapter.ForumAdapter
import com.example.infracare.databinding.FragmentForumBinding
import com.example.infracare.model.ForumPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_POST = 101

    private val db = FirebaseFirestore.getInstance()
    private var forumListener: ListenerRegistration? = null

    private val forumAdapter = ForumAdapter(mutableListOf())
    private var allPosts = mutableListOf<ForumPost>() // Data asli

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ubah warna ●
        val spannable = SpannableString("●    Forum")
        val colorSecondary = ContextCompat.getColor(requireContext(), R.color.secondary)
        spannable.setSpan(ForegroundColorSpan(colorSecondary), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.root.findViewById<TextView>(R.id.forumTitleTextView).text = spannable

        // Setup RecyclerView
        binding.forumRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.forumRecyclerView.adapter = forumAdapter

        // Ambil data
        loadForumPosts()

        // Tombol Buat Postingan
        binding.btnBuatPostingan.setOnClickListener {
            val intent = Intent(requireContext(), BuatPostinganActivity::class.java)
            startActivityForResult(intent, REQUEST_POST)
        }

        // Search button click
        binding.btnSearch.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            filterPosts(query)
        }

    }

    private fun loadForumPosts() {
        binding.forumProgressBar.visibility = View.VISIBLE
        forumListener = db.collection("forum")
            .addSnapshotListener { snapshots, e ->
                binding.forumProgressBar.visibility = View.GONE
                if (e != null) {
                    // Log error kalau perlu
                    return@addSnapshotListener
                }

                val posts = mutableListOf<ForumPost>()
                snapshots?.forEach { doc ->
                    val id = doc.id
                    val nama = doc.getString("nama") ?: "Tidak diketahui"
                    val tanggal = doc.getString("tanggal") ?: ""
                    val lokasi = doc.getString("lokasi") ?: ""
                    val judul = doc.getString("judul") ?: ""
                    val isi = doc.getString("isi") ?: ""
                    val urlGambar = doc.getString("urlGambar")
                    val jumlahKomentar = doc.getLong("jumlahKomentar")?.toInt() ?: 0

                    posts.add(
                        ForumPost(
                            id = id,
                            nama = nama,
                            tanggal = tanggal,
                            lokasi = lokasi,
                            judul = judul,
                            isi = isi,
                            urlGambar = urlGambar,
                            jumlahKomentar = jumlahKomentar
                        )
                    )
                }

                allPosts = posts
                forumAdapter.updateData(posts)
            }
    }

    private fun filterPosts(query: String) {
        val filtered = if (query.isEmpty()) {
            allPosts
        } else {
            allPosts.filter { post ->
                post.judul.contains(query, ignoreCase = true) ||
                        post.isi.contains(query, ignoreCase = true) ||
                        post.nama.contains(query, ignoreCase = true)
            }
        }
        forumAdapter.updateData(filtered)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_POST && resultCode == Activity.RESULT_OK) {
            val isFromForum = data?.getBooleanExtra("from_forum", false) == true
            if (isFromForum) {
                (activity as? MainActivity)?.showPopupNotification("Forum Anda Terkirim")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        forumListener?.remove()
        _binding = null
    }
}
