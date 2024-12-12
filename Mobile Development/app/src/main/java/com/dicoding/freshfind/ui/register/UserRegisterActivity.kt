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
import com.dicoding.freshfind.databinding.ActivityRegisterUserBinding
import com.dicoding.freshfind.network.ApiClient
import com.dicoding.freshfind.network.UserRegisterRequest
import com.dicoding.freshfind.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserBinding
    private val apiService = ApiClient.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using View Binding
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
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
            val name = binding.edtNama.text.toString().trim()
            val number = binding.edtPhone.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val confirmPassword = binding.edtPasswordUlang.text.toString().trim()
            val addressNumber = binding.edtAddressNumber.text.toString().trim()
            val addressStreet = binding.edtAddressStreet.text.toString().trim()
            val addressVillage = binding.edtAddressVillage.text.toString().trim()
            val addressSubdistrict = binding.edtAddressSubdistrict.text.toString().trim()
            val addressCity = binding.edtAddressCity.text.toString().trim()
            val addressProvince = binding.edtAddressProvince.text.toString().trim()
            val addressCode = binding.edtAddressCode.text.toString().trim()

            if (listOf(name, number, email, password, confirmPassword, addressNumber, addressStreet,
                    addressVillage, addressSubdistrict, addressCity, addressProvince, addressCode)
                    .any { it.isEmpty() }) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(
                name = name,
                number = number,
                email = email,
                password = password,
                addressNumber = addressNumber.toInt(),
                addressStreet = addressStreet,
                addressVillage = addressVillage,
                addressSubdistrict = addressSubdistrict,
                addressCity = addressCity,
                addressProvince = addressProvince,
                addressCode = addressCode.toInt()
            )
        }

        // Pindah ke Seller Register
        binding.pindahSeller.setOnClickListener {
            startActivity(Intent(this, SellerRegisterActivity::class.java))
        }

        // Pindah ke Login
        binding.pindahLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser(
        name: String,
        number: String,
        email: String,
        password: String,
        addressNumber: Int,
        addressStreet: String,
        addressVillage: String,
        addressSubdistrict: String,
        addressCity: String,
        addressProvince: String,
        addressCode: Int
    ) {
        val request = UserRegisterRequest(
            number = number,
            password = password,
            email = email,
            name = name,
            birthdate = "2000-01-01",
            address_number = addressNumber,
            address_street = addressStreet,
            address_village = addressVillage,
            address_subdistrict = addressSubdistrict,
            address_city = addressCity,
            address_province = addressProvince,
            address_code = addressCode
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.userRegister(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        Toast.makeText(
                            this@UserRegisterActivity,
                            "Register Failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UserRegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        finish() }
}