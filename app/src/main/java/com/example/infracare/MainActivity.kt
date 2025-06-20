package com.example.infracare

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Default Fragment
        loadFragment(HomeFragment())

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.laporanku -> loadFragment(LaporankuFragment())
                R.id.forum -> loadFragment(ForumFragment())
                R.id.profile -> loadFragment(ProfileFragment())
            }
            true
        }

        fab.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}