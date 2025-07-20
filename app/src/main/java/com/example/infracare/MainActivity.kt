package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default Fragment saat pertama kali
        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // Navigasi BottomNav
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.laporanku -> loadFragment(LaporankuFragment())
                R.id.forum -> loadFragment(ForumFragment())
                R.id.profile -> loadFragment(ProfileFragment())
            }
            true
        }

        // Aksi FAB ke CameraActivity
        fab.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        // ✅ Tampilkan popup notifikasi jika user datang dari form laporan
        if (intent.getBooleanExtra("show_popup", false)) {
            // Delay agar muncul setelah layout selesai dirender
            window.decorView.postDelayed({
                showPopupNotification()
            }, 500)
        }
    }

    // Fungsi untuk load fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Fungsi untuk tampilkan popup animasi
    fun showPopupNotification() {
        val popup = findViewById<LinearLayout>(R.id.popupContainer)
        popup.visibility = View.VISIBLE
        popup.alpha = 0f
        popup.translationY = -popup.height.toFloat()

        // Muncul dari atas
        popup.animate()
            .alpha(1f)
            .translationY(100f)
            .setDuration(300)
            .withEndAction {
                // Menghilang ke atas setelah 3 detik
                popup.postDelayed({
                    popup.animate()
                        .alpha(0f)
                        .translationY(-popup.height.toFloat())
                        .setDuration(300)
                        .withEndAction {
                            popup.visibility = View.GONE
                        }
                }, 3000)
            }
            .start()
    }
}
