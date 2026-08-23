package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "salon_idolos")
data class IdolEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val rutaFoto: String = "", // Ruta local de la foto del autor (almacenamiento/galería) o recurso
    val biografia: String,
    val porqueMeEncanto: String, // Apartado especial destacado
    val epoca: String = "",
    val corriente: String = "",
    val fraseCelebre: String = "",
    val obrasPrincipales: String = "", // Obras separadas por coma
    val fechaRegistro: Long = System.currentTimeMillis()
)
