package com.angel.smartcloset.data.repository

import com.angel.smartcloset.data.model.*
import com.angel.smartcloset.data.network.RetrofitHelper
import com.angel.smartcloset.data.network.SmartClosetApiService

/**
 * El Repositorio es el único punto de contacto con el servidor para la lógica del armario.
 * Su trabajo es centralizar todas las llamadas a la API.
 */
class SmartClosetRepository {

    /** Aquí "instanciamos" la conexión.
     * Llamamos a nuestro RetrofitHelper para obtener la llamada configurada
     * y le pasamos nuestra interfaz (SmartClosetApiService) para que Retrofit
     * construya todas las funciones.
     */
    private val api = RetrofitHelper.getBackendRetrofit().create(SmartClosetApiService::class.java)

    /**
     * Todas estas funciones son "suspend".
     * Eso significa que son funciones asíncronas (corrutinas). Cuando el ViewModel llama
     * a estas funciones, el hilo principal de la aplicación
     * no se queda bloqueado esperando. La app sigue fluida mientras esto se ejecuta en segundo plano.
     */

    suspend fun getRecomendaciones(token: String, idFirebase: String, clima: String, temperatura: Double) = api.getRecomendaciones(token, idFirebase, clima, temperatura)
    suspend fun getArmario(token: String, idFirebase: String) = api.getArmario(token, idFirebase)
    suspend fun addPrenda(token: String, request: AddPrendaRequest) = api.addPrenda(token, request)
    suspend fun deletePrenda(token: String, idPrenda: Int) = api.deletePrenda(token, idPrenda)
    suspend fun updatePrenda(token: String, idPrenda: Int, request: UpdatePrendaRequest) = api.updatePrenda(token, idPrenda, request)
}