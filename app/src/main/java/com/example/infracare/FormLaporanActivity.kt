package com.example.infracare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private val LOCATION_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_laporan)

        imageView = findViewById(R.id.imageView)
        edtJudul = findViewById(R.id.edtJudul)
        edtDeskripsi = findViewById(R.id.edtDeskripsi)
        edtLokasi = findViewById(R.id.edtLokasi)
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi)

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

        // Klik tombol konfirmasi
        btnKonfirmasi.setOnClickListener {
            val judul = edtJudul.text.toString().trim()
            val deskripsi = edtDeskripsi.text.toString().trim()
            val lokasi = edtLokasi.text.toString().trim()

            if (judul.isEmpty() || deskripsi.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua form", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulasi pengiriman laporan (ganti dengan logika asli jika ada)
            val sukses = true // ubah ke false untuk simulasi gagal

            val message = if (sukses) {
                "Laporan berhasil dikirim."
            } else {
                "Laporan gagal dikirim. Silakan coba lagi."
            }

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    if (sukses) {
                        // Kembali ke halaman utama (ubah sesuai nama Activity kamu)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        dialog.dismiss()
                    }
                }
                .setCancelable(false)
                .show()
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
