package com.angel.smartcloset.ui.wardrobe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angel.smartcloset.data.model.Prenda
import com.angel.smartcloset.data.model.UpdatePrendaRequest
import com.angel.smartcloset.data.repository.SmartClosetRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Representa los posibles estados de la app.
 */
sealed class WardrobeState {
    object Loading : WardrobeState()
    data class Success(val prendas: List<Prenda>) : WardrobeState()
    data class Error(val message: String) : WardrobeState()
}

/**
 * Esta clase implementa la lógica de la pantalla del armario.
 */
class WardrobeViewModel(private val repository: SmartClosetRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WardrobeState>(WardrobeState.Loading)
    val uiState: StateFlow<WardrobeState> = _uiState.asStateFlow()

    /**
     * Obtiene el listado de prendas del usuario.
     *
     *  - Si el usuario no está autenticado, no hace nada. Return@launch
     *  - Intenta obtener el token de Firebase. Si falla, no hace nada.
     *  - Realiza una petición al repositorio para obtener el listado de prendas.
     *  - Si la respuesta es exitosa, actualiza el estado a Success. Si no actualiza a error.
     *
     */
    fun fetchArmario() {
        viewModelScope.launch {
            val user = Firebase.auth.currentUser ?: return@launch
            try {
                val token = user.getIdToken(true).await().token ?: ""

                val response = repository.getArmario("Bearer $token", user.uid)

                if (response.isSuccessful) {
                    _uiState.value = WardrobeState.Success(response.body() ?: emptyList())
                } else {
                    _uiState.value = WardrobeState.Error("Error al cargar: código ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = WardrobeState.Error(e.message ?: "Error de conexión al obtener el armario")
            }
        }
    }

    /**
     * Elimina una prenda del armario a través de su ID.
     *  - Si el usuario no está autenticado, no hace nada. Return@launch
     *  - Intenta obtener el token de Firebase. Si falla, no hace nada.
     *  - Realiza una petición al repositorio para eliminar la prenda.
     *  - Si la respuesta es exitosa, recarga el armario para actualizar la lista en la UI.
     *  - Si la respuesta no es exitosa, da error .
     */
    fun deletePrenda(idPrenda: Int) {
        viewModelScope.launch {
            val user = Firebase.auth.currentUser ?: return@launch
            try {
                val token = user.getIdToken(true).await().token ?: ""
                val response = repository.deletePrenda("Bearer $token", idPrenda)

                if (response.isSuccessful) {
                    fetchArmario()
                } else {
                    _uiState.value = WardrobeState.Error("No se pudo eliminar la prenda: código ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = WardrobeState.Error(e.message ?: "Error de red al intentar eliminar la prenda")
            }
        }
    }

    /**
     * Actualiza la categoría y el color de una prenda existente.
     *  - Si el usuario no está autenticado, no hace nada. Return@launch
     *  - Intenta obtener el token de Firebase. Si falla, no hace nada.
     *  - Crea un objeto UpdatePrendaRequest con los nuevos valores de categoría y color.
     *  - Realiza una petición al repositorio para actualizar la prenda.
     *  - Si la respuesta es exitosa, recarga el armario para actualizar la lista en la UI.
     *  - Si la respuesta no es exitosa, da error.
     */
    fun updatePrenda(idPrenda: Int, categoria: String, color: String) {
        viewModelScope.launch {
            val user = Firebase.auth.currentUser ?: return@launch
            try {
                val token = user.getIdToken(true).await().token ?: ""
                val request = UpdatePrendaRequest(categoria, color)
                val response = repository.updatePrenda("Bearer $token", idPrenda, request)

                if (response.isSuccessful) {
                    fetchArmario()
                } else {
                    _uiState.value = WardrobeState.Error("No se pudo actualizar la prenda: código ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = WardrobeState.Error(e.message ?: "Error de red al intentar actualizar la prenda")
            }
        }
    }
}