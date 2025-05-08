package com.dogAPPackage.dogapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.dogAPPackage.dogapp.R

class  MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)
        val biometricManager = BiometricManager.from(this)

        when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                lottieAnimationView.setOnClickListener{
                    showBiometricPrompt()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                showToast("No hay hardware biometrico")

            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                showToast("Hardware biometrico no disponible")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                showToast("No hay biometria registrada")
            }
        }
    }
    private fun showBiometricPrompt(){
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    showToast("Autenticación exitosa")

                    //direccionamiento a pagina de administrador de citas

                    //val intent = Intent(this@MainActivity, administrar::class.java )
                    //startActivity(intent)
                    //finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast(" Error $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("Autenticacion fallida")
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con Biometría")
            .setSubtitle("Ingrese su huella digital")
            .setNegativeButtonText("Cancelar")

            .build()
        biometricPrompt.authenticate(promptInfo)
    }
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}