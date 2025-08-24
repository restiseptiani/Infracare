package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNamaLengkap: EditText
    private lateinit var etNik: EditText
    private lateinit var etNoTelepon: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var cbSyaratKetentuan: CheckBox
    private lateinit var btnKirim: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var  masukTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()

    private var isNikValid = false
    private var isNoTeleponValid = false
    private var isPasswordValid = false
    private var isEmailUnique = false
    private var isNikUnique = false

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNamaLengkap = findViewById(R.id.nameEditText)
        etNik = findViewById(R.id.nikEditText)
        etNoTelepon = findViewById(R.id.hpEditText)
        etEmail = findViewById(R.id.emailEditText)
        etPassword = findViewById(R.id.passwordEditText)
        etConfirmPassword = findViewById(R.id.confirmpasswordEditText)
        cbSyaratKetentuan = findViewById(R.id.cb_syaratketentuan)
        btnKirim = findViewById(R.id.btn_daftar)
        progressBar = findViewById(R.id.progressBar)
        masukTextView = findViewById(R.id.tv_Masuk)

        // Listener NIK
        etNik.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nik = s.toString().trim()
                if (nik.length == 16 && nik.all { it.isDigit() }) {
                    firestore.collection("users").whereEqualTo("nik", nik)
                        .get()
                        .addOnSuccessListener { docs ->
                            isNikUnique = docs.isEmpty
                            if (!isNikUnique) {
                                etNik.error = "NIK sudah terdaftar"
                                isNikValid = false
                            } else {
                                etNik.error = null
                                isNikValid = true
                            }
                            updateButtonState()
                        }
                } else {
                    etNik.error = "NIK harus 16 angka"
                    isNikValid = false
                    isNikUnique = false
                    updateButtonState()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener No Telepon
        etNoTelepon.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val telp = s.toString().trim()
                if (telp.length >= 10 && telp.all { it.isDigit() }) {
                    etNoTelepon.error = null
                    isNoTeleponValid = true
                } else {
                    etNoTelepon.error = "Nomor telepon minimal 10 angka"
                    isNoTeleponValid = false
                }
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener Email
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    firestore.collection("users").whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { docs ->
                            isEmailUnique = docs.isEmpty
                            if (!isEmailUnique) {
                                etEmail.error = "Email sudah terdaftar"
                            } else {
                                etEmail.error = null
                            }
                            updateButtonState()
                        }
                } else {
                    etEmail.error = "Format email tidak valid"
                    isEmailUnique = false
                    updateButtonState()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Listener Password & Confirm Password
        val passwordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pass = etPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()
                if (pass.isNotEmpty() && pass == confirmPass) {
                    etConfirmPassword.error = null
                    isPasswordValid = true
                } else {
                    etConfirmPassword.error = "Password tidak sama"
                    isPasswordValid = false
                }
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        etPassword.addTextChangedListener(passwordWatcher)
        etConfirmPassword.addTextChangedListener(passwordWatcher)

        // Listener Checkbox
        cbSyaratKetentuan.setOnCheckedChangeListener { _, _ ->
            updateButtonState()
        }

        // Toggle password visibility for etPassword
        etPassword.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (etPassword.right - etPassword.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    togglePasswordVisibility(etPassword, isPasswordVisible)
                    isPasswordVisible = !isPasswordVisible
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Toggle password visibility for etConfirmPassword
        etConfirmPassword.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (etConfirmPassword.right - etConfirmPassword.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    togglePasswordVisibility(etConfirmPassword, isConfirmPasswordVisible)
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Klik tombol
        btnKirim.setOnClickListener {
            btnKirim.isEnabled = false
            progressBar.visibility = View.VISIBLE

            val email = etEmail.text.toString().trim()
            val otp = Random.nextInt(1000, 9999).toString()

            sendOtpEmail(email, otp) { success ->
                if (success) {
                    val intent = Intent(this, VerificationActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("otp", otp)
                    intent.putExtra("nik", etNik.text.toString().trim())
                    intent.putExtra("nama", etNamaLengkap.text.toString().trim())
                    intent.putExtra("noHp", etNoTelepon.text.toString().trim())
                    intent.putExtra("password", etPassword.text.toString().trim())
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengirim OTP", Toast.LENGTH_SHORT).show()
                    btnKirim.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        }

        masukTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateButtonState() {
        btnKirim.isEnabled =
            isNikValid && isNoTeleponValid && isPasswordValid &&
                    isEmailUnique && isNikUnique && cbSyaratKetentuan.isChecked
    }

    private fun togglePasswordVisibility(editText: EditText, currentlyVisible: Boolean) {
        if (currentlyVisible) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_eyes)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            editText.setCompoundDrawables(null, null, drawable, null)
        } else {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_show_eye)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            editText.setCompoundDrawables(null, null, drawable, null)
        }
        editText.setSelection(editText.text.length)
    }

    private fun sendOtpEmail(email: String, otp: String, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val props = java.util.Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = javax.mail.Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        return javax.mail.PasswordAuthentication(
                            "infracare.noreply@gmail.com",
                            "yjwarjxjebhblodh"
                        )
                    }
                })

                val message = javax.mail.internet.MimeMessage(session).apply {
                    setFrom(javax.mail.internet.InternetAddress("infracare.noreply@gmail.com"))
                    setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(email))
                    subject = "Kode OTP Pendaftaran"
                    setText("Kode OTP Anda adalah: $otp")
                }

                javax.mail.Transport.send(message)
                runOnUiThread { callback(true) }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { callback(false) }
            }
        }.start()
    }
}