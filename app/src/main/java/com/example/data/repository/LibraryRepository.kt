package com.example.data.repository

import android.content.Context
import com.example.data.dao.BookDao
import com.example.data.dao.BookNoteDao
import com.example.data.datasource.BooksJsonManager
import com.example.data.datasource.IdolsDataSource
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.IdolAuthor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LibraryRepository(
    private val bookDao: BookDao,
    private val bookNoteDao: BookNoteDao,
    private val context: Context
) {

    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()
    val allNotes: Flow<List<BookNoteEntity>> = bookNoteDao.getAllNotes()

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = bookDao.getBookCount()
        if (count == 0) {
            val initialData = BooksJsonManager.loadInitialCatalog(context)
            if (initialData != null && initialData.books.isNotEmpty()) {
                bookDao.insertBooks(initialData.books)
                if (initialData.notes.isNotEmpty()) {
                    bookNoteDao.insertNotes(initialData.notes)
                }
            }
        }
    }

    fun getBooksByCategory(category: String): Flow<List<BookEntity>> {
        return bookDao.getBooksByCategory(category)
    }

    fun getBookById(id: String): Flow<BookEntity?> {
        return bookDao.getBookById(id)
    }

    suspend fun getBookByIdSync(id: String): BookEntity? = withContext(Dispatchers.IO) {
        bookDao.getBookByIdSync(id)
    }

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return bookDao.searchBooks(query)
    }

    fun getNotesForBook(bookId: String): Flow<List<BookNoteEntity>> {
        return bookNoteDao.getNotesForBook(bookId)
    }

    fun getNotesForPage(bookId: String, pageNumber: Int): Flow<List<BookNoteEntity>> {
        return bookNoteDao.getNotesForPage(bookId, pageNumber)
    }

    suspend fun insertBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookDao.updateBook(book)
    }

    suspend fun updateRating(bookId: String, rating: Int) = withContext(Dispatchers.IO) {
        bookDao.updateRating(bookId, rating.coerceIn(1, 5))
    }

    suspend fun updateProgress(bookId: String, page: Int, isFinished: Boolean) = withContext(Dispatchers.IO) {
        bookDao.updateProgress(bookId, page, isFinished)
    }

    suspend fun deleteBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookNoteDao.deleteNotesByBookId(book.id)
        bookDao.deleteBook(book)
    }

    suspend fun deleteBookById(id: String) = withContext(Dispatchers.IO) {
        bookNoteDao.deleteNotesByBookId(id)
        bookDao.deleteBookById(id)
    }

    suspend fun insertNote(note: BookNoteEntity) = withContext(Dispatchers.IO) {
        bookNoteDao.insertNote(note)
    }

    suspend fun deleteNote(note: BookNoteEntity) = withContext(Dispatchers.IO) {
        bookNoteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: String) = withContext(Dispatchers.IO) {
        bookNoteDao.deleteNoteById(id)
    }

    // Salón de los Ídolos
    fun getAllIdols(): List<IdolAuthor> {
        return IdolsDataSource.IDOLS
    }

    fun getIdolById(id: String): IdolAuthor? {
        return IdolsDataSource.getById(id)
    }

    // Import / Export JSON
    suspend fun importJsonData(jsonString: String, replaceExisting: Boolean = false): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val parsed = BooksJsonManager.parseLibraryJsonString(jsonString)
            if (replaceExisting) {
                bookDao.clearAll()
            }
            if (parsed.books.isNotEmpty()) {
                bookDao.insertBooks(parsed.books)
                if (parsed.notes.isNotEmpty()) {
                    bookNoteDao.insertNotes(parsed.notes)
                }
            }
            Result.success(parsed.books.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportCurrentDatabase(): String = withContext(Dispatchers.IO) {
        // Collect current books and notes synchronously
        val parsed = BooksJsonManager.loadInitialCatalog(context)
        val initialBooks = parsed?.books ?: emptyList()
        val initialNotes = parsed?.notes ?: emptyList()
        BooksJsonManager.exportToJsonString(initialBooks, initialNotes)
    }

    suspend fun resetToDefaultCatalog() = withContext(Dispatchers.IO) {
        bookDao.clearAll()
        val initialData = BooksJsonManager.loadInitialCatalog(context)
        if (initialData != null) {
            bookDao.insertBooks(initialData.books)
            bookNoteDao.insertNotes(initialData.notes)
        }
    }
}
