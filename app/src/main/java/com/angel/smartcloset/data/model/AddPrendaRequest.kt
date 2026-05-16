package com.angel.smartcloset.data.model

import com.google.gson.annotations.SerializedName

/**
 * Esta clase es un DTO (Data Transfer Object).
 * La usamos exclusivamente para empaquetar los datos y enviarlos a nuestro servidor
 * cuando el usuario toma una foto y queremos guardar una nueva prenda en el backend.
 */
data class AddPrendaRequest(
    // El ID del usuario logueado en Firebase.
    @SerializedName("idFirebase")
    val idFirebase: String,

    @SerializedName("urlImagen")
    val urlImagen: String
)