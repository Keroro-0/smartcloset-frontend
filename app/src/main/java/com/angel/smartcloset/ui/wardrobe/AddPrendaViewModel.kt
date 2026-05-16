package com.angel.smartcloset.ui.wardrobe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angel.smartcloset.data.model.AddPrendaRequest
import com.angel.smartcloset.data.repository.SmartClosetRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Representa los posibles estados de la app.
 *  - Idle: No hay ninguna acción en curso.
 *  - Loading: La app está cargando algo.
 *  - Success: La acción fue exitosa.
 *  - Error: Ocurrió un error.
 */
sealed class AddPrendaState {
    object Idle : AddPrendaState()
    data class Loading(val message: String) : AddPrendaState()
    object Success : AddPrendaState()
    data class Error(val message: String) : AddPrendaState()
}

/**
 * Esta clase implementa la lógica para añadir prendas.
 * Creamos las vairbales (privada / pública) para administrar el estado,
 */

class AddPrendaViewModel(
    private val repository: SmartClosetRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPrendaState>(AddPrendaState.Idle)
    val uiState: StateFlow<AddPrendaState> = _uiState.asStateFlow()

    /**
     * Sube la imagen a Firebase Storage y luego envía la URL al backend para que Gemini analice la prenda.
     *  - Si el usuario no está autenticado, no hace nada. Return@launch
     *  - Intenta obtener el token de Firebase. Si falla, no hace nada.
     */
    fun uploadAndAnalyzeImage(imageBytes: ByteArray) {
        viewModelScope.launch {

            val user = Firebase.auth.currentUser
            if (user == null) {
                _uiState.value = AddPrendaState.Error("Sesión expirada.")
                return@launch
            }

            val storage: com.google.firebase.storage.FirebaseStorage
            try {
                storage = Firebase.storage
            } catch (e: Exception) {
                _uiState.value = AddPrendaState.Error("Firebase Storage no configurado: ${e.message}")
                return@launch
            }

            /**
             * Indicamos el estado de carga para subir la imagen.
             *
             * - Se genera una referencia única para la imagen en Firebase Storage.
             * - Se sube la imagen a la referencia generada.
             * - Se obtiene la URL pública de la imagen que hemos subido.
             * - Actualizamos el mensaje de carga para que el usuario sepa que ahora la IA está trabajando.
             * - Obtenemos el token de sesión del usuario.
             */
            _uiState.value = AddPrendaState.Loading("Subiendo foto a la nube...")

            try {
                val imageRef = storage.reference
                    .child("armario/${user.uid}/${UUID.randomUUID()}.jpg")

                val uploadSnapshot = imageRef.putBytes(imageBytes).await()

                val downloadUrl = uploadSnapshot.storage.downloadUrl.await().toString()

                _uiState.value = AddPrendaState.Loading("Gemini está analizando tu prenda...")

                val tokenResult = user.getIdToken(true).await()
                val token = tokenResult.token

                /**
                 * Si el token del usuario no es nulo
                 * - Se crea un objeto AddPrendaRequest con la URL de la imagen y el ID del usuario.
                 * - Se hace una llamada al servidor para añadir la prenda al armario.
                 * - Si la respuesta es exitosa, se notifica el éxito.
                 * - Si la respuesta no es exitosa, se indíca el error.
                 */

                if (token != null) {
                    val request = AddPrendaRequest(idFirebase = user.uid, urlImagen = downloadUrl)

                    val response = repository.addPrenda("Bearer $token", request)

                    if (response.isSuccessful) {
                        _uiState.value = AddPrendaState.Success
                    } else {
                        _uiState.value = AddPrendaState.Error("Error del servidor: ${response.code()}")
                    }
                } else {
                    _uiState.value = AddPrendaState.Error("Error de autenticación.")
                }

            } catch (e: com.google.firebase.storage.StorageException) {
                Log.e("SmartCloset", "StorageException código: ${e.errorCode}, mensaje: ${e.message}", e)
                _uiState.value = AddPrendaState.Error("Storage error ${e.errorCode}: ${e.message}")
            } catch (e: Exception) {
                Log.e("SmartCloset", "Error general: ${e.javaClass.simpleName}: ${e.message}", e)
                _uiState.value = AddPrendaState.Error("Error: ${e.javaClass.simpleName} - ${e.message}")
            }
        }
    }

    /**
     * Resetea el estado de la UI para volver a la pantalla inicial después de un éxito o error.
     */
    fun resetState() {
        _uiState.value = AddPrendaState.Idle
    }
}