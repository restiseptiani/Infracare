package com.example.infracare

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.infracare.api.CloudinaryApiService
import com.example.infracare.network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private lateinit var ivProfile: ImageView
    private lateinit var etNik: EditText
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelepon: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private val firestore = FirebaseFirestore.getInstance()
    private var userId: String? = null

    private val cloudName = "dzofhsgzp"
    private val uploadPreset = "infracare"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        ivProfile = view.findViewById(R.id.ivProfile)
        etNik = view.findViewById(R.id.etNik)
        etNama = view.findViewById(R.id.etNama)
        etEmail = view.findViewById(R.id.etEmail)
        etTelepon = view.findViewById(R.id.etTelepon)
        btnSave = view.findViewById(R.id.btnSave)
        progressBar = view.findViewById(R.id.progressBar)

        // Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserSession", 0)
        userId = sharedPref.getString("nik", null)

        loadUserData()

        ivProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSave.setOnClickListener {
            if (imageUri != null) {
                uploadImageAndSaveProfile(imageUri!!)
            } else {
                saveProfile(null) // update hanya teks
            }
        }

        return view
    }

    private fun loadUserData() {
        userId?.let { id ->
            firestore.collection("users").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        etNik.setText(document.getString("nik"))
                        etNama.setText(document.getString("namaLengkap"))
                        etEmail.setText(document.getString("email"))
                        etTelepon.setText(document.getString("noTelepon"))

                        val fotoUrl = document.getString("fotoProfil")
                        Glide.with(this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.pp)
                            .into(ivProfile)

                        etNik.isEnabled = false
                        etEmail.isEnabled = false
                    }
                }
        }
    }

    private fun uploadImageAndSaveProfile(uri: Uri) {
        progressBar.visibility = View.VISIBLE
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
            val compressedBytes = outputStream.toByteArray()
            outputStream.close()

            val requestFile = compressedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", "profile.jpg", requestFile)

            val service = RetrofitClient.instance.create(CloudinaryApiService::class.java)
            val call = service.uploadImage(cloudName, uploadPreset, body)

            call.enqueue(object : Callback<okhttp3.ResponseBody> {
                override fun onResponse(
                    call: Call<okhttp3.ResponseBody>,
                    response: Response<okhttp3.ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body()?.string() ?: "")
                        val imageUrl = json.getString("secure_url")
                        saveProfile(imageUrl)
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Upload gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile(fotoUrl: String?) {
        val nama = etNama.text.toString()
        val telepon = etTelepon.text.toString()

        if (nama.isEmpty() || telepon.isEmpty()) {
            Toast.makeText(requireContext(), "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mutableMapOf(
            "namaLengkap" to nama,
            "noTelepon" to telepon
        )

        if (fotoUrl != null) updates["fotoProfil"] = fotoUrl

        userId?.let { id ->
            firestore.collection("users").document(id)
                .update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE

                    // ✅ Update juga ke SharedPreferences
                    val sharedPref = requireContext().getSharedPreferences("UserSession", 0)
                    with(sharedPref.edit()) {
                        putString("namaLengkap", nama)
                        putString("noTelepon", telepon)
                        if (fotoUrl != null) {
                            putString("fotoProfil", fotoUrl)
                        }
                        apply()
                    }

                    Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            ivProfile.setImageURI(imageUri)
        }
    }
}
