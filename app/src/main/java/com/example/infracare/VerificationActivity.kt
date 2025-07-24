package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class VerificationActivity : AppCompatActivity() {

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var btnConfirm: Button
    private lateinit var resendLink: TextView
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        btnConfirm = findViewById(R.id.btnConfirm)
        resendLink = findViewById(R.id.resendLink)
        backIcon = findViewById(R.id.backIcon)

        backIcon.setOnClickListener {
            onBackPressed()
        }

        btnConfirm.setOnClickListener {
            val code = otp1.text.toString() + otp2.text.toString() +
                    otp3.text.toString() + otp4.text.toString()

            if (code.length == 4) {
                Toast.makeText(this, "Kode: $code", Toast.LENGTH_SHORT).show()

                // Arahkan ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // menutup VerificationActivity agar tidak bisa kembali
            } else {
                Toast.makeText(this, "Masukkan 4 digit kode", Toast.LENGTH_SHORT).show()
            }
        }

        resendLink.setOnClickListener {
            Toast.makeText(this, "Kode verifikasi dikirim ulang", Toast.LENGTH_SHORT).show()
        }
    }
}
