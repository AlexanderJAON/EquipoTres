package com.dogAPPackage.dogapp.repository

import com.dogAPPackage.dogapp.model.UserRequest
import com.dogAPPackage.dogapp.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun registerUser(userRequest: UserRequest, userResponse: (UserResponse) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                firebaseAuth.createUserWithEmailAndPassword(userRequest.email, userRequest.password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val email = task.result?.user?.email
                            userResponse(
                                UserResponse(
                                    email = email,
                                    isRegister = true,
                                    message = "Registro Exitoso"
                                )
                            )
                        } else {
                            handleRegistrationError(task.exception, userResponse)
                        }
                    }
            } catch (e: Exception) {
                userResponse(
                    UserResponse(
                        isRegister = false,
                        message = e.message ?: "Error desconocido"
                    )
                )
            }
        }
    }

    private fun handleRegistrationError(error: Exception?, callback: (UserResponse) -> Unit) {
        when (error) {
            is FirebaseAuthUserCollisionException -> {
                callback(
                    UserResponse(
                        isRegister = false,
                        message = "El usuario ya existe"
                    )
                )
            }
            else -> {
                callback(
                    UserResponse(
                        isRegister = false,
                        message = "Error en el registro: ${error?.message ?: "Desconocido"}"
                    )
                )
            }
        }
    }
}