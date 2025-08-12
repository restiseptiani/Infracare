package com.example.infracare

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.infracare.api.CloudinaryApiService
import com.example.infracare.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BuatPostinganActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var btnKirim: Button
    private lateinit var btnBack: ImageView
    private lateinit var iconTambah: ImageView
    private lateinit var imgPreview: ImageView
    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val db = FirebaseFirestore.getInstance()

    private val cloudName = "dzofhsgzp" // Ganti sesuai akunmu
    private val uploadPreset = "infracare" // Ganti sesuai preset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_postingan)

        etJudul = findViewById(R.id.etJudul)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        btnKirim = findViewById(R.id.btnKirim)
        btnBack = findViewById(R.id.btnBack)
        iconTambah = findViewById(R.id.ic_add)
        imgPreview = findViewById(R.id.imgPreview)
        progressBar = findViewById(R.id.progressBar)

        // launcher pilih gambar
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data!!.data
                Glide.with(this).load(selectedImageUri).into(imgPreview)
                imgPreview.visibility = View.VISIBLE

                // langsung wrap_content saat gambar muncul
                adjustEtDeskripsiHeight()
            }
        }

        // listener untuk judul (cek tombol kirim)
        etJudul.addTextChangedListener(SimpleTextWatcher {
            updateBtnKirimState()
        })

        // listener untuk deskripsi (auto height + cek tombol kirim)
        etDeskripsi.addTextChangedListener(SimpleTextWatcher {
            adjustEtDeskripsiHeight()
            updateBtnKirimState()
        })

        btnBack.setOnClickListener { finish() }
        iconTambah.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnKirim.setOnClickListener {
            showConfirmationDialog(
                "Kirimkan Postingan Ini?",
                "Pilih Kirim Jika Postingan Sudah Sesuai,\nPilih Tinjau Ulang Jika Belum"
            ) { uploadPost() }
        }
    }

    // fungsi untuk menyesuaikan tinggi etDeskripsi
    private fun adjustEtDeskripsiHeight() {
        etDeskripsi.minHeight = 0
        etDeskripsi.setSingleLine(false)
        etDeskripsi.isSingleLine = false
        etDeskripsi.setHorizontallyScrolling(false)

        etDeskripsi.post {
            val lineCount = etDeskripsi.lineCount
            val lineHeight = etDeskripsi.lineHeight
            val padding = etDeskripsi.paddingTop + etDeskripsi.paddingBottom

            // minimal 4 baris supaya nggak kekecilan
            val minLines = if (imgPreview.visibility == View.VISIBLE) 4 else 2

            val desiredHeight = (lineHeight * maxOf(lineCount, minLines)) + padding

            val params = etDeskripsi.layoutParams
            params.height = desiredHeight
            etDeskripsi.layoutParams = params
        }
    }



    // fungsi update state tombol kirim
    private fun updateBtnKirimState() {
        val isValid = etJudul.text.isNotEmpty() && etDeskripsi.text.isNotEmpty()
        btnKirim.isEnabled = isValid
        btnKirim.backgroundTintList = ContextCompat.getColorStateList(
            this@BuatPostinganActivity,
            if (isValid) R.color.secondary else R.color.grey
        )
    }

    // proses upload post
    private fun uploadPost() {
        val judul = etJudul.text.toString()
        val isi = etDeskripsi.text.toString()
        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val dummyNama = "Resiana"
        val dummyFotoProfil = "pp.jpg"
        val jumlahKomentar = 0

        progressBar.visibility = View.VISIBLE

        if (selectedImageUri != null) {
            compressAndUploadImage(selectedImageUri!!) { imageUrl ->
                savePostToFirestore(judul, isi, tanggal, dummyNama, dummyFotoProfil, jumlahKomentar, imageUrl)
            }
        } else {
            savePostToFirestore(judul, isi, tanggal, dummyNama, dummyFotoProfil, jumlahKomentar, null)
        }
    }

    // kompres dan upload gambar ke Cloudinary
    private fun compressAndUploadImage(uri: Uri, callback: (String?) -> Unit) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()

            val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "upload.jpg", requestFile)

            val service = RetrofitClient.instance.create(CloudinaryApiService::class.java)
            val call = service.uploadImage(cloudName, uploadPreset, filePart)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body()?.string() ?: "")
                        val imageUrl = json.getString("secure_url")
                        callback(imageUrl)
                    } else {
                        Log.e("UPLOAD_ERROR", response.errorBody()?.string() ?: "Unknown error")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("UPLOAD_ERROR", "Failure: ${t.message}", t)
                    callback(null)
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    // simpan data postingan ke Firestore
    private fun savePostToFirestore(
        judul: String, isi: String, tanggal: String,
        nama: String, fotoProfil: String, jumlahKomentar: Int, urlGambar: String?
    ) {
        val postMap = hashMapOf(
            "judul" to judul,
            "isi" to isi,
            "tanggal" to tanggal,
            "nama" to nama,
            "fotoProfil" to fotoProfil,
            "jumlahKomentar" to jumlahKomentar,
            "lokasi" to "Lokasi Dummy"
        )
        if (!urlGambar.isNullOrEmpty()) postMap["urlGambar"] = urlGambar

        db.collection("forum").add(postMap)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                setResult(Activity.RESULT_OK, Intent().putExtra("from_forum", true))
                finish()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal menyimpan ke Firestore", Toast.LENGTH_SHORT).show()
            }
    }

    // dialog konfirmasi
    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        val view = layoutInflater.inflate(R.layout.dialog_konfirmasi, null)
        val dialog = AlertDialog.Builder(this).setView(view).setCancelable(false).create()

        view.findViewById<TextView>(R.id.tvTitle).text = title
        view.findViewById<TextView>(R.id.tvMessage).text = message

        view.findViewById<TextView>(R.id.tvTinjauUlang).setOnClickListener { dialog.dismiss() }
        view.findViewById<TextView>(R.id.tvKirim).setOnClickListener {
            dialog.dismiss(); onConfirm()
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            dialog.window?.setLayout((metrics.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
        dialog.show()
        }
}