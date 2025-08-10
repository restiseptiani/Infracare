package com.example.infracare

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class BuatPostinganActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnKirim: Button
    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var etJudul: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var icAdd: ImageView
    private lateinit var progressBar: ProgressBar

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_postingan)

        btnBack = findViewById(R.id.btnBack)
        btnKirim = findViewById(R.id.btnKirim)
        imgProfile = findViewById(R.id.imgProfile)
        tvName = findViewById(R.id.tvName)
        etJudul = findViewById(R.id.etJudul)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        imgPreview = findViewById(R.id.imgPreview)
        icAdd = findViewById(R.id.ic_add)
        progressBar = findViewById(R.id.progressBar)

        btnBack.setOnClickListener {
            finish()
        }

        icAdd.setOnClickListener {
            openGallery()
        }

        etJudul.addTextChangedListener(SimpleTextWatcher { validateInputs() })
        etDeskripsi.addTextChangedListener(SimpleTextWatcher { validateInputs() })
    }

    private fun validateInputs() {
        val isJudulValid = etJudul.text.toString().trim().isNotEmpty()
        val isDeskripsiValid = etDeskripsi.text.toString().trim().isNotEmpty()
        btnKirim.isEnabled = isJudulValid && isDeskripsiValid
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let {
                imgPreview.visibility = View.VISIBLE
                Glide.with(this).load(it).into(imgPreview)

                // Atur ulang tinggi EditText agar sesuai isi
                etDeskripsi.setMinHeight(0)
                val params = etDeskripsi.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                etDeskripsi.layoutParams = params
                etDeskripsi.invalidate()
                etDeskripsi.requestLayout()

                // Hapus padding vertikal jika perlu
                etDeskripsi.setPadding(
                    etDeskripsi.paddingLeft,
                    8,
                    etDeskripsi.paddingRight,
                    8
                )

                // Paksa resize berdasarkan isi baris
                resizeEditTextToContent(etDeskripsi)
            }
        }
    }

    private fun resizeEditTextToContent(editText: EditText) {
        editText.post {
            val lineHeight = editText.lineHeight
            val lines = editText.lineCount.coerceAtLeast(1) // minimal 1 baris
            val targetHeight = lineHeight * lines + editText.paddingTop + editText.paddingBottom

            val layoutParams = editText.layoutParams
            layoutParams.height = targetHeight
            editText.layoutParams = layoutParams
        }
    }
}
