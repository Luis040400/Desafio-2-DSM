package com.example.desafiopractico2.model

import java.io.Serializable

data class Item(
    var cantidad: Int?,
    var contra_indicaciones: String?,
    var imagen: String?,
    var indicaciones_uso: String?,
    var nombre: String,
    var precio: Float?,
) : Serializable
