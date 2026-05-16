package com.angel.smartcloset.data.model

import com.google.gson.annotations.SerializedName

/**
 * Esta clase representa el modelo principal de una "Prenda" dentro de la aplicación.
 * La usamos para recibir y leer los datos que nos devuelve el servidor (backend)
 * cuando pedimos la lista de ropa del armario del usuario.
 */
data class Prenda(
    @SerializedName("id")
    val id: Int,

    @SerializedName("urlImagen")
    val urlImagen: String,

    @SerializedName("categoria")
    val categoria: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("fechaAdicion")
    val fechaAdicion: String? = null
)