package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val category: String, // Filosofía, Ciencia, Novela, Poesía, Teatro
    val coverUrl: String = "",
    val pdfPath: String = "",
    val rating: Int = 5, // 1 to 5 stars
    val synopsis: String = "",
    val totalPages: Int = 100,
    val currentPage: Int = 1,
    val isFinished: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)
