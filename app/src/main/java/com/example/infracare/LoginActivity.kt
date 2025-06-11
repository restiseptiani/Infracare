package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var rememberCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var googleLoginButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var daftarTextView: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        rememberCheckBox = findViewById(R.id.cb_remember)
        loginButton = findViewById(R.id.btn_login)
        googleLoginButton = findViewById(R.id.btn_google)
        forgotPasswordTextView = findViewById(R.id.tv_lupapassword)
        daftarTextView = findViewById(R.id.tv_daftar)


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan kata sandi wajib diisi!", Toast.LENGTH_SHORT).show()
            } else {

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.error = "Format email tidak valid!"
                } else if (password.length < 8) {
                    passwordEditText.error = "Kata sandi minimal 8 karakter!"
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        googleLoginButton.setOnClickListener {
            Toast.makeText(this, "Login dengan Google", Toast.LENGTH_SHORT).show()
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
                if (email.isEmpty()) {
                    emailEditText.error = null
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.error = "Email tidak valid!"
                } else {
                    emailEditText.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })


        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (password.isEmpty()) {
                    passwordEditText.error = null
                } else if (password.length < 8) {
                    passwordEditText.error = "Password minimal 8 karakter!"
                } else {
                    passwordEditText.error = null
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
}
