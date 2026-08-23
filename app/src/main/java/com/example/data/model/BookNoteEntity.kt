package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "book_notes",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId")]
)
data class BookNoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bookId: String,
    val pageNumber: Int,
    val selectedText: String, // Fragmento subrayado
    val noteText: String = "", // Comentario personal
    val highlightColorHex: String = "#BB86FC", // Color de subrayado
    val timestamp: Long = System.currentTimeMillis()
)
