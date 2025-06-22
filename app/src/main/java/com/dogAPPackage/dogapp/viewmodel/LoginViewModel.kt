package com.dogAPPackage.dogapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dogAPPackage.dogapp.model.UserRequest
import com.dogAPPackage.dogapp.model.UserResponse
import com.dogAPPackage.dogapp.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _isRegister = MutableLiveData<UserResponse>()
    val isRegister: LiveData<UserResponse> = _isRegister

    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            repository.registerUser(userRequest) { userResponse ->
                _isRegister.value = userResponse
            }
        }
    }

    fun loginUser(email: String, pass: String, isLogin: (Boolean) -> Unit) {
        if (email.isNotEmpty() && pass.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        isLogin(true)
                    } else {
                        isLogin(false)
                        val error = task.exception?.message ?: "Error desconocido"
                        Log.e("LoginError", error)
                    }
                }
                .addOnFailureListener { e ->
                    isLogin(false)
                    Log.e("LoginError", "Error de red: ${e.message}")
                }
        } else {
            isLogin(false)
        }
    }

    fun sesion(email: String?, isEnableView: (Boolean) -> Unit) {
        if (email != null) {
            isEnableView(true)
        } else {
            isEnableView(false)
        }
    }
}