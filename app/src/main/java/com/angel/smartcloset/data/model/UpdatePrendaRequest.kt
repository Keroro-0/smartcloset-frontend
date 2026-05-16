package com.angel.smartcloset.data.model

import com.google.gson.annotations.SerializedName

/**
 * Esta clase es un DTO (Data Transfer Object).
 * La usamos cuando el usuario decide editar manualmente una prenda que ya existe en el armario.
 * Se le enviará al backend con los nuevos datos actualizados.
 */
data class UpdatePrendaRequest(
    @SerializedName("categoria")
    val categoria: String,

    @SerializedName("color")
    val color: String
)