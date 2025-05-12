package com.dogAPPackage.dogapp.model

// Este modelo representa la respuesta JSON de la API de razas de perros
data class DogBreedsResponse(
    val message: Map<String, List<String>>,
    val status: String
)
