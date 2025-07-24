package com.example.infracare

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class BuatPostinganActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var btnKirim: Button
    private lateinit var btnBack: ImageView
    private lateinit var iconTambah: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_postingan)

        etJudul = findViewById(R.id.etJudul)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        btnKirim = findViewById(R.id.btnKirim)
        btnBack = findViewById(R.id.btnBack)
        iconTambah = findViewById(R.id.ic_add)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isFormValid = etJudul.text.toString().isNotEmpty() &&
                        etDeskripsi.text.toString().isNotEmpty()
                btnKirim.isEnabled = isFormValid
                btnKirim.backgroundTintList = ContextCompat.getColorStateList(
                    this@BuatPostinganActivity,
                    if (isFormValid) R.color.secondary else R.color.grey
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etJudul.addTextChangedListener(watcher)
        etDeskripsi.addTextChangedListener(watcher)

        btnBack.setOnClickListener {
            finish()
        }

        iconTambah.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnKirim.setOnClickListener {
            showConfirmationDialog(
                title = "Kirimkan Postingan Ini?",
                message = "Pilih Kirim Jika Postingan Sudah Sesuai,\nPilih Tinjau Ulang Jika Postingan \nBelum Sesuai"
            ) {
                // Kirim hasil ke fragment
                val resultIntent = Intent()
                resultIntent.putExtra("from_forum", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val tvTinjauUlang = dialogView.findViewById<TextView>(R.id.tvTinjauUlang)
        val tvKirim = dialogView.findViewById<TextView>(R.id.tvKirim)

        tvTitle.text = title
        tvMessage.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        tvTinjauUlang.setOnClickListener {
            dialog.dismiss()
        }

        tvKirim.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val width = (metrics.widthPixels * 0.75).toInt()

            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Toast.makeText(this, "Gambar berhasil dipilih", Toast.LENGTH_SHORT).show()

            // TODO: tampilkan preview gambar jika dibutuhkan
        }
    }
}
