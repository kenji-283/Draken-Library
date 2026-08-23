package com.example.data.datasource

import android.content.Context
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ParsedLibraryData(
    val books: List<BookEntity>,
    val notes: List<BookNoteEntity>,
    val version: Int = 1,
    val description: String = ""
)

object BooksJsonManager {

    /**
     * Parsea un archivo JSON o InputStream con la estructura canónica de Draken's Library.
     */
    fun parseLibraryJson(inputStream: InputStream): ParsedLibraryData {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return parseLibraryJsonString(jsonString)
    }

    fun parseLibraryJsonString(jsonString: String): ParsedLibraryData {
        val rootObj = JSONObject(jsonString)
        val version = rootObj.optInt("version", 1)
        val description = rootObj.optString("description", "")
        val booksArray = rootObj.optJSONArray("books") ?: JSONArray()

        val parsedBooks = mutableListOf<BookEntity>()
        val parsedNotes = mutableListOf<BookNoteEntity>()

        for (i in 0 until booksArray.length()) {
            val bookObj = booksArray.getJSONObject(i)
            val bookId = bookObj.optString("id", UUID.randomUUID().toString())
            val title = bookObj.optString("title", "Sin título")
            val author = bookObj.optString("author", "Autor Desconocido")
            val category = bookObj.optString("category", "Filosofía")
            val rating = bookObj.optInt("rating", 5).coerceIn(1, 5)
            val synopsis = bookObj.optString("synopsis", "")
            val totalPages = bookObj.optInt("totalPages", 100).coerceAtLeast(1)
            val currentPage = bookObj.optInt("currentPage", 1).coerceIn(1, totalPages)
            val isFinished = bookObj.optBoolean("isFinished", currentPage >= totalPages)
            val coverUrl = bookObj.optString("coverUrl", "")
            val pdfPath = bookObj.optString("pdfPath", "")

            val bookEntity = BookEntity(
                id = bookId,
                title = title,
                author = author,
                category = category,
                coverUrl = coverUrl,
                pdfPath = pdfPath,
                rating = rating,
                synopsis = synopsis,
                totalPages = totalPages,
                currentPage = currentPage,
                isFinished = isFinished,
                dateAdded = System.currentTimeMillis()
            )
            parsedBooks.add(bookEntity)

            val notesArray = bookObj.optJSONArray("notes")
            if (notesArray != null) {
                for (j in 0 until notesArray.length()) {
                    val noteObj = notesArray.getJSONObject(j)
                    val noteId = noteObj.optString("id", UUID.randomUUID().toString())
                    val pageNumber = noteObj.optInt("pageNumber", 1)
                    val selectedText = noteObj.optString("selectedText", "")
                    val noteText = noteObj.optString("noteText", "")
                    val highlightColorHex = noteObj.optString("highlightColorHex", "#BB86FC")

                    parsedNotes.add(
                        BookNoteEntity(
                            id = noteId,
                            bookId = bookId,
                            pageNumber = pageNumber,
                            selectedText = selectedText,
                            noteText = noteText,
                            highlightColorHex = highlightColorHex,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        return ParsedLibraryData(
            books = parsedBooks,
            notes = parsedNotes,
            version = version,
            description = description
        )
    }

    /**
     * Carga el catálogo inicial empaquetado en assets.
     */
    fun loadInitialCatalog(context: Context): ParsedLibraryData? {
        return try {
            context.assets.open("books_database.json").use { inputStream ->
                parseLibraryJson(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Exporta los libros y notas de la biblioteca a formato JSON estructurado
     * compatible con copias de seguridad locales y Google Drive.
     */
    fun exportToJsonString(books: List<BookEntity>, allNotes: List<BookNoteEntity>): String {
        val rootObj = JSONObject()
        rootObj.put("version", 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        rootObj.put("lastUpdated", dateFormat.format(Date()))
        rootObj.put("description", "Draken's Library export data - Google Drive sync compatible")

        val booksArray = JSONArray()
        val notesByBook = allNotes.groupBy { it.bookId }

        for (book in books) {
            val bookObj = JSONObject()
            bookObj.put("id", book.id)
            bookObj.put("title", book.title)
            bookObj.put("author", book.author)
            bookObj.put("category", book.category)
            bookObj.put("rating", book.rating)
            bookObj.put("synopsis", book.synopsis)
            bookObj.put("totalPages", book.totalPages)
            bookObj.put("currentPage", book.currentPage)
            bookObj.put("isFinished", book.isFinished)
            bookObj.put("coverUrl", book.coverUrl)
            bookObj.put("pdfPath", book.pdfPath)

            val notesArray = JSONArray()
            val bookNotes = notesByBook[book.id] ?: emptyList()
            for (note in bookNotes) {
                val noteObj = JSONObject()
                noteObj.put("id", note.id)
                noteObj.put("pageNumber", note.pageNumber)
                noteObj.put("selectedText", note.selectedText)
                noteObj.put("noteText", note.noteText)
                noteObj.put("highlightColorHex", note.highlightColorHex)
                notesArray.put(noteObj)
            }
            bookObj.put("notes", notesArray)
            booksArray.put(bookObj)
        }

        rootObj.put("books", booksArray)
        return rootObj.toString(2)
    }

    /**
     * Guarda el archivo JSON en el almacenamiento interno de la app.
     */
    fun saveToInternalStorage(context: Context, jsonContent: String, fileName: String = "books_database.json"): File {
        val file = File(context.filesDir, fileName)
        file.writeText(jsonContent)
        return file
    }
}
