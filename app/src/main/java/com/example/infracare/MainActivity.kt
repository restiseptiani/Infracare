package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tampilkan default HomeFragment
        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // Navigasi antara fragment
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.laporanku -> loadFragment(LaporankuFragment())
                R.id.forum -> loadFragment(ForumFragment())
                R.id.profile -> loadFragment(ProfileFragment())
            }
            true
        }

        // FAB membuka CameraActivity
        fab.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        // Cek jika berasal dari intent yang ingin menampilkan popup
        if (intent.getBooleanExtra("show_popup", false)) {
            window.decorView.postDelayed({
                showPopupNotification("Laporan Anda Terkirim")
            }, 500)
        }
    }

    // Fungsi ganti fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Fungsi untuk menampilkan popup notifikasi dengan teks dinamis
    fun showPopupNotification(message: String = "Laporan Anda Terkirim") {
        val popup = findViewById<LinearLayout>(R.id.popupContainer)
        val popupText = findViewById<TextView>(R.id.popupText)
        popupText.text = message

        popup.visibility = View.VISIBLE
        popup.alpha = 0f
        popup.translationY = -popup.height.toFloat()

        popup.animate()
            .alpha(1f)
            .translationY(100f)
            .setDuration(300)
            .withEndAction {
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
