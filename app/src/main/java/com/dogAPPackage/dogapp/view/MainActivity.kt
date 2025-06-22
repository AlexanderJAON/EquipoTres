package com.dogAPPackage.dogapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dogAPPackage.dogapp.R
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inicialización de Firebase (si es necesaria)
        FirebaseApp.initializeApp(this)

        // Aquí puedes mantener tu lógica actual del home
    }
}