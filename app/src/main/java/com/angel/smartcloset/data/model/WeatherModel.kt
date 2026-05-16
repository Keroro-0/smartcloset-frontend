package com.angel.smartcloset.data.model

import com.google.gson.annotations.SerializedName

/**
 * Esta clase se usa para interpretar el JSON que devuelve
 * la API del clima (Open-Meteo). Solo nos interesa el bloque "current".
 */
data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeather
)

/**
 * Aquí extraemos los datos específicos del clima que necesitamos delo bloque "current",
 * para pasárselos a la IA y que decida el outfit.
 */
data class CurrentWeather(
    // Temperatura actual en grados.
    @SerializedName("temperature_2m")
    val temperature: Double,

    // Un código numérico que nos dice si hace sol, lluvia, niebla...
    @SerializedName("weather_code")
    val weatherCode: Int
)