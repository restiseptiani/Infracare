package com.example.infracare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var imagePreview: ImageView
    private lateinit var btnCapture: ImageButton
    private lateinit var btnFlash: ImageButton
    private lateinit var btnGallery: ImageButton
    private lateinit var btnRetake: Button
    private lateinit var btnUse: Button
    private lateinit var layoutControls: View
    private lateinit var layoutReview: View
    private lateinit var tvTitle: TextView

    private var imageCapture: ImageCapture? = null
    private var flashEnabled = false
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            imagePreview.setImageURI(it)
            tvTitle.text = "Tinjau Foto"
            showReview()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        imagePreview = findViewById(R.id.imagePreview)
        btnCapture = findViewById(R.id.btnCapture)
        btnFlash = findViewById(R.id.btnFlash)
        btnGallery = findViewById(R.id.btnGallery)
        btnRetake = findViewById(R.id.btnRetake)
        btnUse = findViewById(R.id.btnUse)
        layoutControls = findViewById(R.id.layoutControls)
        layoutReview = findViewById(R.id.layoutReview)
        tvTitle = findViewById(R.id.tvTitle)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
        }

        btnCapture.setOnClickListener { takePhoto() }
        btnGallery.setOnClickListener { pickImage.launch("image/*") }
        btnFlash.setOnClickListener {
            flashEnabled = !flashEnabled
            imageCapture?.flashMode = if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            btnFlash.setImageResource(if (flashEnabled) R.drawable.baseline_flash_on_24 else R.drawable.baseline_flash_off_24)
        }

        btnRetake.setOnClickListener {
            resetCameraView()
            tvTitle.text = "Ambil Foto Laporan"
        }

        btnUse.setOnClickListener {
            selectedImageUri?.let {
                val intent = Intent(this, FormLaporanActivity::class.java)
                intent.putExtra("image_uri", it.toString())
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Foto tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                Log.d("CameraActivity", "Kamera berhasil dimulai")
            } catch (e: Exception) {
                Log.e("CameraActivity", "Gagal memulai kamera", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File.createTempFile("report", ".jpg", cacheDir)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraActivity", "Gagal ambil foto", exc)
                    Toast.makeText(this@CameraActivity, "Gagal ambil foto", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(photoFile)
                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                    tvTitle.text = "Tinjau Foto"
                    showReview()
                }
            }
        )
    }

    private fun showReview() {
        imagePreview.visibility = View.VISIBLE
        previewView.visibility = View.GONE
        layoutControls.visibility = View.GONE
        layoutReview.visibility = View.VISIBLE
    }

    private fun resetCameraView() {
        selectedImageUri = null
        imagePreview.setImageDrawable(null)
        imagePreview.visibility = View.GONE
        previewView.visibility = View.VISIBLE
        layoutControls.visibility = View.VISIBLE
        layoutReview.visibility = View.GONE
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startCamera()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
