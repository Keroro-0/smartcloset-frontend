package com.angel.smartcloset.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Una sealed class se encarga de representar estados.
 * Solo puede ser uno de estos 4.
 * - Idle : Es el estado inicial , el que espera a que pase algo.
 * - Loading : Mientras espera la respuesta del servidor.
 * - Success : Si el servidor nos da datos.
 * - Error : Si algo falla.
 *
 */
sealed class AuthRes {
    object Idle : AuthRes()
    object Loading : AuthRes()
    object Success : AuthRes()
    data class Error(val message: String) : AuthRes()
}

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    /**
     * VARIABLES REACTIVAS (StateFlow):
     * Usamos el patrón de variable privada mutable ( _ ) y pública inmutable .
     * Así evitamos que la pantalla (UI) modifique los datos accidentalmente;
     * solo el ViewModel tiene permiso para cambiar el valor.
     */
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _authState = MutableStateFlow<AuthRes>(AuthRes.Idle)
    val authState: StateFlow<AuthRes> = _authState.asStateFlow()

    /**
     * Funciones para cambiar el valor de las variables cuando el usuario escribe
     */

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Funcion para iniciar sesión con el correo
     */

    fun loginWithEmail() {
        if (!isFormValid()) return

        viewModelScope.launch {
            _authState.value = AuthRes.Loading
            try {
                auth.signInWithEmailAndPassword(_email.value, _password.value).await()
                _authState.value = AuthRes.Success
            } catch (e: Exception) {
                _authState.value = AuthRes.Error(e.localizedMessage ?: "Error de autenticación")
            }
        }
    }

    /**
     * Función para iniciar sesión con Google
     */

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthRes.Loading
            try {
                auth.signInWithCredential(credential).await()
                _authState.value = AuthRes.Success
            } catch (e: Exception) {
                _authState.value = AuthRes.Error("Fallo con Google: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Validación de que los campos no esten null antes de llamar a internet
    */
    private fun isFormValid(): Boolean {
        return if (_email.value.isBlank() || _password.value.isBlank()) {
            _authState.value = AuthRes.Error("Rellena todos los campos")
            false
        } else true
    }
}