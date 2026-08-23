package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val category: String, // Filosofía, Ciencia, Novela, Poesía, Teatro, etc.
    val coverUrl: String = "",
    val pdfPath: String = "", // Ruta local en almacenamiento de la app (si descargado)
    val pdfOnlineUrl: String = "", // Enlace de Google Drive, GitHub o repositorio en línea
    val isDownloaded: Boolean = false, // Determina si ocupa espacio en el dispositivo
    val fileSizeBytes: Long = 1024 * 1024 * 2L, // Tamaño estimado / real en bytes (ej: 2.1 MB)
    val rating: Int = 5, // 1 a 5 estrellas
    val synopsis: String = "",
    val totalPages: Int = 100,
    val currentPage: Int = 1,
    val isFinished: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
