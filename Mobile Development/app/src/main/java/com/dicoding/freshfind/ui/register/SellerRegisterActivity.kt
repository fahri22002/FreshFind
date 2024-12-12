package com.dicoding.freshfind.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.freshfind.databinding.ActivityRegisterSellerBinding
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.network.SellerRegisterRequest
import com.dicoding.freshfind.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SellerRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterSellerBinding
    private val apiService = ApiClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityRegisterSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Real-time validation for "Confirm Password"
        binding.edtPasswordUlang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.edtPassword.text.toString()
                val confirmPassword = s.toString()
                binding.edtPasswordUlangLayout.error =
                    if (password != confirmPassword) "Password tidak sama" else null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle Register Button Click
        binding.btnRegister.setOnClickListener {
            val storeName = binding.edtNamaToko.text.toString().trim()
            val number = binding.edtTelepon.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val confirmPassword = binding.edtPasswordUlang.text.toString().trim()
            val addressNumber = binding.edtNoRumah.text.toString().trim()
            val addressStreet = binding.edtJalan.text.toString().trim()
            val addressVillage = binding.edtDesa.text.toString().trim()
            val addressSubdistrict = binding.edtKelurahan.text.toString().trim()
            val addressCity = binding.edtKota.text.toString().trim()
            val addressProvince = binding.edtProvinsi.text.toString().trim()
            val addressCode = binding.edtKodePos.text.toString().trim()
            val bankAccount = binding.edtNoRekening.text.toString().trim()
            val bankName = binding.edtNamaBank.text.toString().trim()

            if (listOf(storeName, number, email, password, confirmPassword).any { it.isEmpty() }) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerSeller(
                storeName = storeName,
                number = number,
                email = email,
                password = password,
                addressNumber = addressNumber.toInt(),
                addressStreet = addressStreet,
                addressVillage = addressVillage,
                addressSubdistrict = addressSubdistrict,
                addressCity = addressCity,
                addressProvince = addressProvince,
                addressCode = addressCode.toInt(),
                bankAccount = bankAccount,
                bankName = bankName
            )
        }

        // Pindah ke User Register
        binding.pindahUser.setOnClickListener {
            startActivity(Intent(this, UserRegisterActivity::class.java))
        }

        // Pindah ke Login
        binding.pindahLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerSeller(
        storeName: String,
        number: String,
        email: String,
        password: String,
        addressNumber : Int,
        addressStreet : String,
        addressVillage : String,
        addressSubdistrict : String,
        addressCity : String,
        addressProvince : String,
        addressCode : Int,
        bankAccount : String,
        bankName : String
    ) {
        val request = SellerRegisterRequest(
            number = number,
            password = password,
            email = email,
            store_name = storeName,
            birthdate = "2000-01-01",
            address_number = addressNumber,
            address_street = addressStreet,
            address_village = addressVillage,
            address_subdistrict = addressSubdistrict,
            address_city = addressCity,
            address_province = addressProvince,
            address_code = addressCode,
            bank_account = bankAccount,
            bank_name = bankName
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.sellerRegister(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        Toast.makeText(
                            this@SellerRegisterActivity,
                            "Register Failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SellerRegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Registration Successful")
            setMessage("You have successfully registered. Please login to continue.")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                navigateToLogin()
            }
            setCancelable(false)
            show()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        }
}