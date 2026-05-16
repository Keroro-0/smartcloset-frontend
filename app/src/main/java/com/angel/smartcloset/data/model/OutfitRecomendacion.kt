package com.angel.smartcloset.data.model

import com.google.gson.annotations.SerializedName

/**
 * Esta clase sirve para obtener la respuesta que nos da la IA
 * cuando le pedimos la recomendación.
 */
data class OutfitRecomendacion(
    @SerializedName("camiseta")
    val camiseta: String,

    @SerializedName("pantalon")
    val pantalon: String,

    @SerializedName("calzado")
    val calzado: String,

    @SerializedName("motivo")
    val motivo: String
)