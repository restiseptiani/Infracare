package com.example.infracare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.google.android.gms.location.*
import java.util.*

class FormLaporanActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var edtJudul: EditText
    private lateinit var edtDeskripsi: EditText
    private lateinit var edtLokasi: EditText
    private lateinit var btnKonfirmasi: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var scrollView: NestedScrollView

    private val LOCATION_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_laporan)

        imageView = findViewById(R.id.imageView)
        edtJudul = findViewById(R.id.edtJudul)
        edtDeskripsi = findViewById(R.id.edtDeskripsi)
        edtLokasi = findViewById(R.id.edtLokasi)
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi)
        scrollView = findViewById(R.id.scrollView)

        // Scroll otomatis ke inputan yang difokus saat keyboard muncul
        val scrollToFocusedView = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                scrollView.postDelayed({
                    scrollView.smoothScrollTo(0, view.top)
                }, 200)
            }
        }

        edtJudul.onFocusChangeListener = scrollToFocusedView
        edtDeskripsi.onFocusChangeListener = scrollToFocusedView
        edtLokasi.onFocusChangeListener = scrollToFocusedView

        // Ambil gambar dari intent
        val imageUri = intent.getStringExtra("image_uri")
        imageUri?.let {
            imageView.setImageURI(Uri.parse(it))
        }

        // Lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) {
            requestNewLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }

        // Tombol Konfirmasi
        btnKonfirmasi.setOnClickListener {
            val judul = edtJudul.text.toString().trim()
            val deskripsi = edtDeskripsi.text.toString().trim()
            val lokasi = edtLokasi.text.toString().trim()

            if (judul.isEmpty() || deskripsi.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua form", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inflate layout custom dialog
            val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi, null)

            // Buat AlertDialog dengan custom view
            val alertDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            // Ambil referensi tombol di dalam dialog
            val tvKirim = dialogView.findViewById<TextView>(R.id.tvKirim)
            val tvTinjauUlang = dialogView.findViewById<TextView>(R.id.tvTinjauUlang)

            // Aksi tombol Kirim
            tvKirim.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("show_popup", true) // Kirim flag
                startActivity(intent)
                finish()
                alertDialog.dismiss()
            }


            // Aksi tombol Tinjau Ulang
            tvTinjauUlang.setOnClickListener {
                alertDialog.dismiss()
            }

            // ✅ Set ukuran dan background dialog agar tidak full lebar dan rounded
            alertDialog.setOnShowListener {
                // Hilangkan background default agar background custom terlihat
                alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                // Atur lebar dialog menjadi 75% dari lebar layar
                val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
                alertDialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.window?.setGravity(Gravity.CENTER)
            alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            alertDialog.show()

        }

    }


        private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location = locationResult.lastLocation ?: return
                updateLocationToAddress(location)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun updateLocationToAddress(location: Location) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            val address = if (!addressList.isNullOrEmpty()) {
                addressList[0].getAddressLine(0)
            } else {
                "Lat: ${location.latitude}, Long: ${location.longitude}"
            }

            edtLokasi.setText(address)

        } catch (e: Exception) {
            edtLokasi.setText("Gagal mendapatkan alamat.")
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            requestNewLocation()
        } else {
            Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
        }
    }
}
