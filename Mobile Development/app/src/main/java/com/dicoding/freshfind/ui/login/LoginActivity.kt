package com.dicoding.freshfind.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.freshfind.MainActivity
import com.dicoding.freshfind.databinding.ActivityLoginBinding
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.network.LoginRequest
import com.dicoding.freshfind.ui.register.UserRegisterActivity
import com.dicoding.freshfind.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handling window insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setting up the role dropdown
        val roles = arrayOf("User", "Seller")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRole.adapter = adapter

        binding.btnLogin.setOnClickListener {
            val phone = binding.edtPhone.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val role = binding.spinnerRole.selectedItem.toString()

            if (phone.isNotEmpty() && password.isNotEmpty()) {
                login(phone, password, role)
            } else {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.pindahRegister.setOnClickListener {
            startActivity(Intent(this, UserRegisterActivity::class.java))
        }
    }

    private fun login(phone: String, password: String, role: String) {
        val apiService = ApiClient.create()
        val request = LoginRequest(phone, password)
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = if (role == "User") {
                    apiService.userLogin(request)
                } else {
                    apiService.sellerLogin(request)
                }

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        response.body()?.let {
                            val token = it.accessToken // Sesuaikan sesuai respons API
                            SessionManager.saveToken(this@LoginActivity, "Bearer $token")
                            SessionManager.saveLoginStatus(this@LoginActivity, true)
                            SessionManager.saveUserRole(this@LoginActivity, role) // Menyimpan peran pengguna
                            Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                            if (role == "User") navigateToMainActivity() else navigateToSellerActivity()
                        } ?: showToast("Gagal mendapatkan data pengguna.")
                    } else {
                        showToast("Login gagal: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    showToast("Terjadi kesalahan: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToSellerActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
