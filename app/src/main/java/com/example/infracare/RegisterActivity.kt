package com.example.infracare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.infracare.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity()    {

    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private lateinit var masukTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol "Masuk"
        masukTextView = findViewById(R.id.tv_daftar)
        masukTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // Button Google
        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Login dengan Google", Toast.LENGTH_SHORT).show()
        }

        // Validasi input secara real-time
        binding.hpEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 10 && s.toString().isNotEmpty()) {
                    binding.hpEditText.error = "Nomor Telepon Tidak Valid"
                } else {
                    binding.hpEditText.error = null
                }
                toggleButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() && s.toString().isNotEmpty()) {
                    binding.emailEditText.error = "Format Email Tidak Valid"
                } else {
                    binding.emailEditText.error = null
                }
                toggleButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length < 8 && s.toString().isNotEmpty()) {
                    binding.passwordEditText.error = "Password minimal 8 karakter"
                } else {
                    binding.passwordEditText.error = null
                }
                toggleButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.confirmpasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = binding.passwordEditText.text.toString()
                val confirmPassword = s.toString()
                if (confirmPassword != password && s.toString().isNotEmpty()) {
                    binding.confirmpasswordEditText.error = "Password Tidak Sama"
                } else {
                    binding.confirmpasswordEditText.error = null
                }
                toggleButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Show/Hide Password
        binding.passwordEditText.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // index of drawableEnd
                if (event.rawX >= (binding.passwordEditText.right - binding.passwordEditText.compoundDrawables[drawableEnd].bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    updatePasswordVisibility(binding.passwordEditText, isPasswordVisible)
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.confirmpasswordEditText.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val drawableEnd = 2 // index of drawableEnd
                if (event.rawX >= (binding.confirmpasswordEditText.right - binding.confirmpasswordEditText.compoundDrawables[drawableEnd].bounds.width())) {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible
                    updatePasswordVisibility(binding.confirmpasswordEditText, isConfirmPasswordVisible)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Checkbox listener
        binding.cbSyaratketentuan.setOnCheckedChangeListener { _, _ ->
            toggleButtonState()
        }

        // Button daftar
        binding.btnLogin.setOnClickListener {
            if (isFormValid()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Awal state
        toggleButtonState()
    }

    private fun updatePasswordVisibility(editText: EditText, isVisible: Boolean) {
        val icon = if (isVisible) R.drawable.ic_show_eye else R.drawable.ic_eyes
        editText.inputType = if (isVisible)
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        else
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val drawable = ContextCompat.getDrawable(this, icon)
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
        editText.setSelection(editText.text.length)
    }

    private fun toggleButtonState() {
        val isFormFilled = binding.nameEditText.text!!.isNotEmpty() &&
                binding.hpEditText.text!!.isNotEmpty() &&
                binding.emailEditText.text!!.isNotEmpty() &&
                binding.passwordEditText.text!!.isNotEmpty() &&
                binding.confirmpasswordEditText.text!!.isNotEmpty() &&
                binding.cbSyaratketentuan.isChecked &&
                binding.hpEditText.error == null &&
                binding.emailEditText.error == null &&
                binding.passwordEditText.error == null &&
                binding.confirmpasswordEditText.error == null

        binding.btnLogin.isEnabled = isFormFilled
        binding.btnLogin.alpha = if (isFormFilled) 1f else 0.5f
    }

    private fun isFormValid(): Boolean {
        return binding.hpEditText.error == null &&
                binding.emailEditText.error == null &&
                binding.passwordEditText.error == null &&
                binding.confirmpasswordEditText.error == null &&
                binding.cbSyaratketentuan.isChecked
    }
}