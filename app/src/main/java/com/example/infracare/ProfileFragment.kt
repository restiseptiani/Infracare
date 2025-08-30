package com.example.infracare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val profileName = view.findViewById<TextView>(R.id.profileName)
        val profileEmail = view.findViewById<TextView>(R.id.profileEmail)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val layoutBahasa = view.findViewById<LinearLayout>(R.id.layoutBahasa)
        val ivProfile = view.findViewById<ImageView>(R.id.profileImage)

        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val namaLengkap = sharedPref.getString("namaLengkap", "User")
        val fotoProfil = sharedPref.getString("fotoProfil", "")
        val email = sharedPref.getString("email","")
        // Dummy data
        profileName.text = "$namaLengkap"
        profileEmail.text = "$email"

        if (!fotoProfil.isNullOrEmpty()) {
            android.util.Log.d("HomeFragment", "Foto profil dari SharedPref: $fotoProfil")
            Glide.with(requireContext())
                .load(fotoProfil)
                .placeholder(R.drawable.pp)
                .error(R.drawable.pp)
                .into(ivProfile)
        } else {
            ivProfile.setImageResource(R.drawable.pp)
        }
        // Atur judul dengan simbol ● berwarna secondary
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val fullText = "●    Profile"
        val spannable = SpannableString(fullText)
        val secondaryColor = ContextCompat.getColor(requireContext(), R.color.secondary)
        spannable.setSpan(
            ForegroundColorSpan(secondaryColor),
            0, 1, // hanya karakter ●
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvTitle.text = spannable

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        layoutBahasa.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }

        return view
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvMessage)
        val tvTinjauUlang = dialogView.findViewById<TextView>(R.id.tvTinjauUlang)
        val tvKirim = dialogView.findViewById<TextView>(R.id.tvKirim)

        tvTitle.text = "Logout dari aplikasi?"
        tvMessage.text = "Pilih Logout untuk keluar dari aplikasi.\nPilih Batal jika ingin tetap masuk."
        tvTinjauUlang.text = "Batal"
        tvKirim.text = "Logout"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        tvTinjauUlang.setOnClickListener {
            dialog.dismiss()
        }

        tvKirim.setOnClickListener {
            dialog.dismiss()
            logout()
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val metrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
            val width = (metrics.widthPixels * 0.75).toInt()

            dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }

        dialog.show()
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_session", 0)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(requireContext(), StartedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
