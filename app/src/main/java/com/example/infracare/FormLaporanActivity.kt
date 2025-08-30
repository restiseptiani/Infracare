package com.example.infracare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.example.infracare.network.RetrofitClient
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private lateinit var progressBar: ProgressBar
    private lateinit var spinnerRTRW: Spinner
    private lateinit var ivProfile: ImageView

    private val LOCATION_PERMISSION_CODE = 100
    private var imageUriString: String? = null
    private var selectedRTRW: String? = null

    private val cloudName = "dzofhsgzp"
    private val uploadPreset = "infracare"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_laporan)

        imageView = findViewById(R.id.imageView)
        edtJudul = findViewById(R.id.edtJudul)
        edtDeskripsi = findViewById(R.id.edtDeskripsi)
        edtLokasi = findViewById(R.id.edtLokasi)
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi)
        scrollView = findViewById(R.id.scrollView)
        progressBar = findViewById(R.id.progressBar)
        spinnerRTRW = findViewById(R.id.spinnerRTRW)
        ivProfile = findViewById(R.id.profileImage)

        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val fotoProfil = sharedPref.getString("fotoProfil", "")

        // isi gambar
        imageUriString = intent.getStringExtra("image_uri")
        imageUriString?.let { imageView.setImageURI(Uri.parse(it)) }

        // auto scroll ke input aktif
        val scrollToFocusedView = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) scrollView.postDelayed({ scrollView.smoothScrollTo(0, view.top) }, 200)
        }
        edtJudul.onFocusChangeListener = scrollToFocusedView
        edtDeskripsi.onFocusChangeListener = scrollToFocusedView
        edtLokasi.onFocusChangeListener = scrollToFocusedView

        // location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) requestNewLocation()
        else ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_CODE
        )

        // isi spinner RT/RW
        setupSpinnerRTRW()

        if (!fotoProfil.isNullOrEmpty()) {
            android.util.Log.d("HomeFragment", "Foto profil dari SharedPref: $fotoProfil")
            Glide.with(this)
                .load(fotoProfil)
                .placeholder(R.drawable.pp)
                .error(R.drawable.pp)
                .into(ivProfile)
        } else {
            ivProfile.setImageResource(R.drawable.pp)
        }

        // tombol konfirmasi
        btnKonfirmasi.setOnClickListener {
            val judul = edtJudul.text.toString().trim()
            val deskripsi = edtDeskripsi.text.toString().trim()
            val lokasi = edtLokasi.text.toString().trim()
            val rtrw = selectedRTRW

            if (judul.isEmpty() || deskripsi.isEmpty() || lokasi.isEmpty() || rtrw.isNullOrEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua form", Toast.LENGTH_SHORT).show()
            } else {
                showConfirmationDialog()
            }
        }
    }

    private fun setupSpinnerRTRW() {
        val rtRwList = mutableListOf<String>()

        // RW 1 - 6 ada 4 RT
        for (rw in 1..6) {
            for (rt in 1..4) {
                rtRwList.add("RW $rw - RT $rt")
            }
        }
        // RW 7 - 8 ada 3 RT
        for (rw in 7..8) {
            for (rt in 1..3) {
                rtRwList.add("RW $rw - RT $rt")
            }
        }

        // Custom adapter pakai layout XML
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,          // item yang dipilih (warna putih)
            rtRwList
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item) // dropdown (warna hitam)

        spinnerRTRW.adapter = adapter

        spinnerRTRW.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedRTRW = rtRwList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedRTRW = null
            }
        }
    }


    private fun showConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<TextView>(R.id.tvKirim).setOnClickListener {
            alertDialog.dismiss()
            uploadImageAndSaveToFirestore()
        }
        dialogView.findViewById<TextView>(R.id.tvTinjauUlang).setOnClickListener { alertDialog.dismiss() }

        alertDialog.setOnShowListener {
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
            alertDialog.window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        alertDialog.window?.setGravity(Gravity.CENTER)
        alertDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
    }

    private fun uploadImageAndSaveToFirestore() {
        if (imageUriString.isNullOrEmpty()) {
            Toast.makeText(this, "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show()
            Log.e("UPLOAD", "imageUriString is null or empty")
            return
        }

        showLoading(true)
        Log.d("UPLOAD", "Start upload process, imageUriString=$imageUriString")

        try {
            val inputStream = contentResolver.openInputStream(Uri.parse(imageUriString))
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val outputStream = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                val compressedBytes = outputStream.toByteArray()
                outputStream.close()

                val requestFile = compressedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", "upload.jpg", requestFile)

                val service = RetrofitClient.instance.create(com.example.infracare.api.CloudinaryApiService::class.java)
                val call = service.uploadImage(cloudName, uploadPreset, body)

                call.enqueue(object : Callback<okhttp3.ResponseBody> {
                    override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
                        showLoading(false)
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()
                            try {
                                val json = org.json.JSONObject(responseBody ?: "")
                                val imageUrl = json.getString("secure_url")
                                saveLaporanToFirestore(imageUrl)
                            } catch (e: Exception) {
                                Toast.makeText(this@FormLaporanActivity, "Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = response.errorBody()?.string()
                            Toast.makeText(this@FormLaporanActivity, "Upload gagal: $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@FormLaporanActivity, "Upload gagal: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } else {
                showLoading(false)
                Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            showLoading(false)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLaporanToFirestore(imageUrl: String) {
        val db = FirebaseFirestore.getInstance()

        // Ambil data user dari SharedPreferences
        val session = getSharedPreferences("UserSession", MODE_PRIVATE)
        val currentNik = session.getString("nik", null)
        val currentNama = session.getString("namaLengkap", null)

        if (currentNik == null || currentNama == null) {
            Toast.makeText(this, "Gagal: data user tidak lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        // Pisahkan RW & RT dari string selectedRTRW
        val rw = selectedRTRW?.substringAfter("RW ")?.substringBefore(" -") ?: ""
        val rt = selectedRTRW?.substringAfter("RT ") ?: ""

        val laporan = hashMapOf(
            "judul" to edtJudul.text.toString(),
            "deskripsi" to edtDeskripsi.text.toString(),
            "lokasi" to edtLokasi.text.toString(),
            "rw" to rw,
            "rt" to rt,
            "status" to "Diterima",
            "tanggal" to getTodayDate(),
            "imageUrl" to imageUrl,
            "nik" to currentNik,
            "nama" to currentNama
        )

        db.collection("laporan")
            .add(laporan)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Berhasil mengirim laporan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("show_popup", true))
                finish()
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(this, "Gagal simpan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getTodayDate(): String {
        val c = Calendar.getInstance()
        return String.format("%02d-%02d-%d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun checkLocationPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                updateLocationToAddress(location)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun updateLocationToAddress(location: Location) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses?.getOrNull(0)?.getAddressLine(0)
                ?: "Lat: ${location.latitude}, Long: ${location.longitude}"
            edtLokasi.setText(address)
        } catch (e: Exception) {
            edtLokasi.setText("Gagal mendapatkan alamat.")
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestNewLocation()
        } else {
            Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
        }
    }
}