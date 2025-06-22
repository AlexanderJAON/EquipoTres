package com.dogAPPackage.dogapp.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dogAPPackage.dogapp.R
import com.dogAPPackage.dogapp.databinding.ActivityLoginBinding
import com.dogAPPackage.dogapp.model.UserRequest
import com.dogAPPackage.dogapp.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

        setButtonsEnabled(false)
        checkSession()
        setupListeners()
        setupObservers()
        setupTextWatchers()
    }

    private fun checkSession() {
        val email = sharedPreferences.getString("email", null)
        loginViewModel.sesion(email) { isEnableView ->
            if (isEnableView) {
                goToHome()
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.tvRegister.setOnClickListener {
            registerUser()
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun setupObservers() {
        loginViewModel.isRegister.observe(this) { userResponse ->
            if (userResponse.isRegister) {
                Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().putString("email", userResponse.email).apply()
                goToHome()
                finish()
            } else {
                //Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
                //Error en el registro
                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }
        }

        binding.etEmail.addTextChangedListener(textWatcher)
        binding.etPass.addTextChangedListener(textWatcher)
    }

    private fun checkFieldsForEmptyValues() {
        val emailFilled = binding.etEmail.text.isNotEmpty()
        val passFilled = binding.etPass.text.isNotEmpty()

        setButtonsEnabled(emailFilled && passFilled)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.tvRegister.isClickable = enabled
        binding.tvRegister.alpha = if (enabled) 1.0f else 0.5f

        if (enabled) {
            binding.btnLogin.setTextColor(ContextCompat.getColor(this, R.color.blanco))
            binding.btnLogin.setTypeface(null, Typeface.BOLD)
        }else{
            binding.btnLogin.setTypeface(null, Typeface.NORMAL)
        }
    }
    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        val userRequest = UserRequest(email, pass)

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            loginViewModel.registerUser(userRequest)
        } else {
            Toast.makeText(this, "Campos Vacíos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPass.text.toString()
        loginViewModel.loginUser(email, pass) { isLogin ->
            if (isLogin) {
                sharedPreferences.edit().putString("email", email).apply()
                goToHome()
                finish()
            } else {
                Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}