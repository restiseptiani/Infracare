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

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_POST = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ubah warna icon ● menjadi warna secondary
        val titleText = "●    Forum"
        val spannable = SpannableString(titleText)
        val colorSecondary = ContextCompat.getColor(requireContext(), R.color.secondary)
        spannable.setSpan(
            ForegroundColorSpan(colorSecondary),
            0,
            1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.root.findViewById<TextView>(R.id.forumTitleTextView).text = spannable

        // Dummy data
        val dummyData = listOf(
            ForumPost(
                1,
                "Yor Forger",
                "13 Nov 2024",
                "Jakarta",
                "Apa Teknologi Ramah Lingkungan yang Paling Berdampak Menurut Anda?",
                "Dunia terus menciptakan teknologi baru untuk melawan perubahan iklim, seperti energi surya, mobil listrik, dan bangunan berkonsep hijau...",
                "https://images.unsplash.com/photo-1603791440384-56cd371ee9a7",
                15
            ),
            ForumPost(
                2,
                "Toji Fushiguro",
                "11 Okt 2024",
                "Lampung",
                "Peran Energi Terbarukan dalam Menyelamatkan Planet Kita",
                "Penggunaan energi terbarukan seperti matahari, angin, dan air menjadi kunci dalam mengurangi emisi karbon...",
                null,
                15
            )
        )

        // Atur RecyclerView
        binding.forumRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.forumRecyclerView.adapter = ForumAdapter(dummyData)

        // Aksi tombol "Buat Postingan"
        binding.btnBuatPostingan.setOnClickListener {
            val intent = Intent(requireContext(), BuatPostinganActivity::class.java)
            startActivityForResult(intent, REQUEST_POST)
        }
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
        _binding = null
    }
}
