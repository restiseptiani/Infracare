package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class VerificationActivity : AppCompatActivity() {

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var btnVerifikasi: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        btnVerifikasi = findViewById(R.id.btnConfirm)
        progressBar = findViewById(R.id.progressBar)

        val nama = intent.getStringExtra("nama") ?: ""
        val nik = intent.getStringExtra("nik") ?: ""
        val noTelp = intent.getStringExtra("noHp") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val otpKirim = intent.getStringExtra("otp") ?: ""

        setupOtpMove(otp1, otp2)
        setupOtpMove(otp2, otp3)
        setupOtpMove(otp3, otp4)
        setupOtpMove(otp4, null)

        btnVerifikasi.setOnClickListener {
            val otpInput = otp1.text.toString().trim() +
                    otp2.text.toString().trim() +
                    otp3.text.toString().trim() +
                    otp4.text.toString().trim()

            if (otpInput == otpKirim) {
                // Disable button, show loading
                btnVerifikasi.isEnabled = false
                progressBar.visibility = android.view.View.VISIBLE

                // Hash password dulu sebelum simpan
                val hashedPassword = hashPassword(password)

                val userMap = hashMapOf<String, Any>(
                    "namaLengkap" to nama,
                    "nik" to nik,
                    "noTelepon" to noTelp,
                    "email" to email,
                    "password" to hashedPassword,
                    "role" to "warga"
                )

                // Simpan ke Firestore (pakai nik sebagai dokumen id biar unik)
                db.collection("users")
                    .document(nik)
                    .set(userMap)
                    .addOnSuccessListener {
                        progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = android.view.View.GONE
                        btnVerifikasi.isEnabled = true
                        Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "OTP salah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOtpMove(current: EditText, next: EditText?) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    next?.requestFocus()
                } else if (s?.isEmpty() == true) {
                    current.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}