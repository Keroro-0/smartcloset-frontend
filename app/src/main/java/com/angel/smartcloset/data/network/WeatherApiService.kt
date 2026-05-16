package com.angel.smartcloset.data.network

import com.angel.smartcloset.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * INTERFAZ: Retrofit leerá este archivo y construirá automáticamente
 * todo el código complejo necesario para conectarse a Internet basándose en las
 * etiquetas (@) que le pongamos aquí.
 *
 *  - @GET : Indicamos que queremos leer infromación del servidor.
 *  - @Query : Añade variables al final de la URL, es decir información extra.
 */

interface WeatherApiService {
    @GET("v1/forecast?current=temperature_2m,weather_code")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): Response<WeatherResponse>
}