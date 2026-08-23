package com.example.data.model

data class IdolAuthor(
    val id: String,
    val nombre: String,
    val rutaDeFoto: String, // Resource name or URL
    val biografia: String,
    val porqueMeEncanto: String,
    val epoca: String = "",
    val corriente: String = "",
    val fraseCelebre: String = "",
    val obrasPrincipales: List<String> = emptyList()
)
