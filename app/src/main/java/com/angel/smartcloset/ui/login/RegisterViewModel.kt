package com.angel.smartcloset.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    /**
     * Inicializa la instancia de Firebase para gestionar la autenticación
     *
     * VARIABLES REACTIVAS (StateFlow):
     * Usamos el patrón de variable privada mutable ( _ ) y pública inmutable .
     * Así evitamos que la pantalla (UI) modifique los datos accidentalmente;
     * solo el ViewModel tiene permiso para cambiar el valor.
     */

    private val auth: FirebaseAuth = Firebase.auth

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _authState = MutableStateFlow<AuthRes>(AuthRes.Idle)
    val authState = _authState.asStateFlow()

    /**
     * Funciones que van actualizando las variables según lo que esriba el usuario
     */
    fun onEmailChange(it: String) { _email.value = it }
    fun onPasswordChange(it: String) { _password.value = it }
    fun onConfirmPasswordChange(it: String) { _confirmPassword.value = it }

    /**
     * Función para registrar un nuevo usuario con Firebase
     *
     *  - Verifica que ningún campo esté vacío
     *  - Verifica que ambas contraseñas coincidan
     *  - Lanza en un nuevo hilo (viewModelScope)
     *      + Muestra un mensaje de carga mientras espera la respuesta del servidor
     *      + Intenta crear el usuario con Firebase pasandole el email y la contraseña
     *      + Si todo va bien, notifica el éxito a la UI
     */

    fun signUp() {
        val mail = _email.value
        val pass = _password.value
        val confirm = _confirmPassword.value

        if (mail.isBlank() || pass.isBlank() || confirm.isBlank()) {
            _authState.value = AuthRes.Error("Por favor, rellena todos los campos")
            return
        }

        if (pass != confirm) {
            _authState.value = AuthRes.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthRes.Loading
            try {
                auth.createUserWithEmailAndPassword(mail, pass).await()
                _authState.value = AuthRes.Success
            } catch (e: Exception) {
                _authState.value = AuthRes.Error(e.localizedMessage ?: "Error al crear cuenta")
            }
        }
    }
}