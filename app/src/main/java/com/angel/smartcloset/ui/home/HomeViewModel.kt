package com.angel.smartcloset.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angel.smartcloset.data.model.CurrentWeather
import com.angel.smartcloset.data.model.OutfitRecomendacion
import com.angel.smartcloset.data.repository.SmartClosetRepository
import com.angel.smartcloset.data.repository.WeatherRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Una `sealed class` limita las opciones a las que definimos aquí.
 * La pantalla observará esto y dibujará cosas diferentes dependiendo del estado.
 */


/**
 * Estados:
 *  - Idle : Es el estado inicial , el que espera a que pase algo.
 *  - Loading : Mientras espera la respuesta del servidor.
 *  - Success : Si el servidor nos da datos.
 *  - EmptyCloset : Si el armario está vacío.
 *  - Error : Si algo falla.
 */

sealed class OutfitState {
    object Idle : OutfitState()
    object Loading : OutfitState()
    data class Success(val outfits: List<OutfitRecomendacion>) : OutfitState()
    object EmptyCloset : OutfitState()
    data class Error(val message: String) : OutfitState()
}


/**
 * El ViewModel es el intermediario entre la UI y los datos.
 */
class HomeViewModel : ViewModel() {

    /**
     * Instanciamos a los Repositorios que se encargan de comunicar con el servidor.
     */
    private val weatherRepository = WeatherRepository()
    private val smartClosetRepository = SmartClosetRepository()

    /**
     * VARIABLES REACTIVAS (StateFlow):
     * Usamos el patrón de variable privada mutable ( _weatherState / _outfitState ) y pública inmutable ( weatherState / outfitState ).
     * Así evitamos que la pantalla (UI) modifique los datos accidentalmente;
     * solo el ViewModel tiene permiso para cambiar el valor.
     */
    private val _weatherState = MutableStateFlow<CurrentWeather?>(null)
    val weatherState: StateFlow<CurrentWeather?> = _weatherState

    private val _outfitState = MutableStateFlow<OutfitState>(OutfitState.Idle)
    val outfitState: StateFlow<OutfitState> = _outfitState.asStateFlow()

    /**
     * FetchWeather : Esta función es la que se encarga de pedir el clima al servidor.
     *
     * Se le tiene que pasar la latitud y longitud y esta actualiza la variable del clima (weatherState),
     * viewModelScope lanza la terea en segundo plano y guardamos el clima en _weatherState.
     * En el caso de que todo funcione bien llama a fetchRecomendaciones pasandole el clima y la temperatura.
     */
    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val weatherResponse = weatherRepository.getClima(lat, lon)
            _weatherState.value = weatherResponse?.current

            if (weatherResponse?.current != null) {
                fetchRecomendaciones(
                    traducirClima(weatherResponse.current.weatherCode),
                    weatherResponse.current.temperature
                )
            } else {
                _outfitState.value = OutfitState.Error("No se pudo obtener el clima.")
            }
        }
    }

    /**
     * FetchRecomendaciones : Conseguir las recomendaciones del servidor.
     * Es "private suspend" porque solo la puede llamar el ViewModel por dentro y funciona de forma asíncrona.
     *
     */
    private suspend fun fetchRecomendaciones(clima: String, temperatura: Double) {
        _outfitState.value = OutfitState.Loading

        val user = Firebase.auth.currentUser
        if (user == null) {
            _outfitState.value = OutfitState.Error("Sesión expirada. Entra de nuevo.")
            return
        }

        /**
         * Obtenemos el token de Firebase
         * Este token nos sirbe para que el backend se fie de nosotros.
         *
         * Si obtenemos el token correctamente, llamamos a la funcion getRecomendaciones del repositorio.
         *  - Si obtenemos respuesta satisfactoria cod 200. Guardamos la lista de outfits en el estado Success
         *  - Si obtenemos respuesta 400. El armario está vacío. Guardamos el estado EmptyCloset
         *  - Si obtenemos respuesta 401. El token caducó o es falso. Guardamos el estado Error
         *  - Cualquier otro error. Guardamos el estado Error
         */

        try {
            val tokenResult = user.getIdToken(true).await()
            val token = tokenResult.token

            if (token != null) {
                val tokenHeader = "Bearer $token"
                val uid = user.uid

                val response = smartClosetRepository.getRecomendaciones(tokenHeader, uid, clima, temperatura)

                if (response.isSuccessful && response.body() != null) {
                    _outfitState.value = OutfitState.Success(response.body()!!)
                } else if (response.code() == 400) {
                    _outfitState.value = OutfitState.EmptyCloset
                } else if (response.code() == 401) {
                    _outfitState.value = OutfitState.Error("No autorizado: El backend no acepta el token.")
                } else {
                    _outfitState.value = OutfitState.Error("Backend Error: ${response.code()}")
                }
            } else {
                _outfitState.value = OutfitState.Error("Error: No se pudo generar el Token de seguridad.")
            }

        } catch (e: Exception) {
            _outfitState.value = OutfitState.Error("Error: ${e.message}")
        }
    }

    /**
     * La API de Open-Meteo nos da números y esta función lo traduce a palabras.
     */
    private fun traducirClima(code: Int): String {
        return when (code) {
            0 -> "Soleado"
            1, 2, 3 -> "Nublado"
            45, 48 -> "Niebla"
            else -> "Templado"
        }
    }
}