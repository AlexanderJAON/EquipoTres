package com.dogAPPackage.dogapp.model

data class DogImageResponse(
    val message: String,  // URL de la imagen
    val status: String    // "success" o "error"
)
