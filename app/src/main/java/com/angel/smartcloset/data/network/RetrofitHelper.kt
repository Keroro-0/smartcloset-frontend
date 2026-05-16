package com.angel.smartcloset.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit es la librería estándar en Android para conectarse a Internet.
 * Aquí se configura todo lo relacionado con las conexiones a Internet.
 */

object RetrofitHelper {

    /**
     * Aquí se configura el timepo de espera que tendrá la conexión.
     * Le decimos que espere hasta 300 segundos si el servidor tarda en responder.
     * Esto se tiene que hacer así porque al mandar cosas a la IA hay veces que tarda mucho en responder
     */
    private val aumentarTiempoConexion = OkHttpClient.Builder()
        .connectTimeout(300, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * GetRetrofit es donde hacemos las llamadas a la API del clima (Open-Meteo).
     * - baseUrl : Tenemos que pasarle la URL de la API.
     * - addConverterFactory : Conversor de JSON a objetos Kotlin.
     */
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * GetBackendRetrofit es donde hacemos las llamadas al backend.
     *  - baseUrl : Tenemos que pasarle la URL del backend.
     *  - aumentarTiempoConexion : Tenemos que pasarle la variable que hemos creado anteriormente para que tenga mas tiempo de espera.
     *  - addConverterFactory : Conversor de JSON a objetos Kotlin.
     */

    fun getBackendRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.143:8080/")
            .client(aumentarTiempoConexion)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}