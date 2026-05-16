package com.angel.smartcloset.data.repository

import com.angel.smartcloset.data.model.WeatherResponse
import com.angel.smartcloset.data.network.RetrofitHelper
import com.angel.smartcloset.data.network.WeatherApiService

/**
 * Gestiona la información meteorológica.
 */
class WeatherRepository {

    /** Aquí "instanciamos" la conexión.
     * Llamamos a nuestro RetrofitHelper para obtener la llamada configurada
     * y le pasamos nuestra interfaz (WeatherApiService) para que Retrofit
     * construya todas las funciones.
     */
    private val api = RetrofitHelper.getRetrofit().create(WeatherApiService::class.java)

    /**
     * Función asíncrona (suspend) que pide el clima basándose en las coordenadas del usuario.
     *
     * El `?` al final de `WeatherResponse?` significa que la función
     * puede devolver los datos del clima O puede devolver un valor nulo si algo falla.
     */
    suspend fun getClima(lat: Double, lon: Double): WeatherResponse? {

        /**
         * Hacemos la llamada por internet pasándole la latitud y longitud.
         */
        val respuesta = api.getWeather(lat, lon)

        /**
         * Si responde.isSuccessful es true (la comunicación fue perfecta), extraemos y devolvemos el .body() (nuestros datos).
         * Si hubo un error, devolvemos `null` para no provocar un cierre inesperado de la app.
         */
        return (if (respuesta.isSuccessful){
            respuesta.body()
                } else null
        )
    }
}