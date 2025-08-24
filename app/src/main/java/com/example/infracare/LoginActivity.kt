package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var daftarTextView: TextView
    private lateinit var progressBar: ProgressBar

    private var isPasswordVisible = false
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        rememberCheckBox = findViewById(R.id.cb_remember)
        loginButton = findViewById(R.id.btn_login)
        forgotPasswordTextView = findViewById(R.id.tv_lupapassword)
        daftarTextView = findViewById(R.id.tv_daftar)
        progressBar = findViewById(R.id.progressBar)  // pastikan ada progressBar di layout

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan kata sandi wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Format email tidak valid!"
                return@setOnClickListener
            }

            if (password.length < 8) {
                passwordEditText.error = "Kata sandi minimal 8 karakter!"
                return@setOnClickListener
            }

            // Mulai proses login
            loginButton.isEnabled = false
            progressBar.visibility = android.view.View.VISIBLE

            db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    progressBar.visibility = android.view.View.GONE
                    if (documents.isEmpty) {
                        loginButton.isEnabled = true
                        Toast.makeText(this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show()
                    } else {
                        val userDoc = documents.documents[0]
                        val passwordHashFirestore = userDoc.getString("password")

                        val inputPasswordHash = hashPassword(password)

                        if (inputPasswordHash == passwordHashFirestore) {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            loginButton.isEnabled = true
                            Toast.makeText(this, "Kata sandi salah", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                .addOnFailureListener { e ->
                    progressBar.visibility = android.view.View.GONE
                    loginButton.isEnabled = true
                    Toast.makeText(this, "Gagal login: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Lupa Kata Sandi diklik", Toast.LENGTH_SHORT).show()
        }

        daftarTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        passwordEditText.setOnTouchListener { v, event ->
            val DRAWABLE_END = 2 // 0=start, 1=top, 2=end, 3=bottom
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                emailEditText.error = when {
                    email.isEmpty() -> null
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email tidak valid!"
                    else -> null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                passwordEditText.error = when {
                    password.isEmpty() -> null
                    password.length < 8 -> "Password minimal 8 karakter!"
                    else -> null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_eyes)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            passwordEditText.setCompoundDrawables(null, null, drawable, null)
            isPasswordVisible = false
        } else {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            val drawable = ContextCompat.getDrawable(this, R.drawable.ic_show_eye)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            passwordEditText.setCompoundDrawables(null, null, drawable, null)
            isPasswordVisible = true
        }
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}