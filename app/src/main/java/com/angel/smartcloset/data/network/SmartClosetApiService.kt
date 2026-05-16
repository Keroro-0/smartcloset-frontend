package com.angel.smartcloset.data.network

import com.angel.smartcloset.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * INTERFAZ: Retrofit leerá este archivo y construirá automáticamente
 * todo el código complejo necesario para conectarse a Internet basándose en las
 * etiquetas (@) que le pongamos aquí.
 *
 *  - @GET : Indicamos que queremos leer infromación del servidor.
 *  - @POST : Indicamos que queremos crear o enviar algo al servidor.
 *  - @PUT : Indicamos que queremos actualizar algo en el servidor.
 *  - @DELETE : Indicamos que queremos borrar algo del servidor.
 *
 *  - @Header : Añade información oculta en la "cabecera" de la petición.
 *  - @Path : Sustituye los parámetros de la URL.
 *  - @Query : Añade variables al final de la URL, es decir información extra.
 *  - @Body : Toma un objeto Kotlin, lo transforma en un archivo JSON y lo mete en el "cuerpo" del envío. Los datos van ocultos, no en la URL.
 */

interface SmartClosetApiService {
    @GET("api/outfits/recomendaciones/{idFirebase}")
    suspend fun getRecomendaciones(
        @Header("Authorization") token: String,
        @Path("idFirebase") idFirebase: String,
        @Query("clima") clima: String,
        @Query("temperatura") temperatura: Double
    ): Response<List<OutfitRecomendacion>>

    @GET("api/armario/{idFirebase}")
    suspend fun getArmario(
        @Header("Authorization") token: String,
        @Path("idFirebase") idFirebase: String
    ): Response<List<Prenda>>

    @POST("api/armario/anadir")
    suspend fun addPrenda(
        @Header("Authorization") token: String,
        @Body request: AddPrendaRequest
    ): Response<Prenda>

    @DELETE("api/armario/borrar/{idPrenda}")
    suspend fun deletePrenda(
        @Header("Authorization") token: String,
        @Path("idPrenda") idPrenda: Int
    ): Response<Unit>

    @PUT("api/armario/actualizar/{idPrenda}")
    suspend fun updatePrenda(
        @Header("Authorization") token: String,
        @Path("idPrenda") idPrenda: Int,
        @Body request: UpdatePrendaRequest
    ): Response<Prenda>
}