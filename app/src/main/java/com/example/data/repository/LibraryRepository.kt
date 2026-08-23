package com.example.data.repository

import android.content.Context
import com.example.data.dao.BookDao
import com.example.data.dao.BookNoteDao
import com.example.data.dao.IdolDao
import com.example.data.datasource.BooksJsonManager
import com.example.data.datasource.IdolsDataSource
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.IdolEntity
import com.example.pdf.PdfHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class LibraryRepository(
    private val bookDao: BookDao,
    private val bookNoteDao: BookNoteDao,
    private val idolDao: IdolDao,
    private val context: Context
) {
    private val pdfHelper = PdfHelper(context)

    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()
    val downloadedBooks: Flow<List<BookEntity>> = bookDao.getDownloadedBooks()
    val allNotes: Flow<List<BookNoteEntity>> = bookNoteDao.getAllNotes()
    val allIdols: Flow<List<IdolEntity>> = idolDao.getAllIdols()
    val totalStorageUsedBytes: Flow<Long?> = bookDao.getTotalStorageUsedBytes()

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        // La aplicación arranca 100% vacía en la primera instalación según especificación.
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

    suspend fun downloadBook(book: BookEntity): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val (file, size) = pdfHelper.downloadBookToStorage(book)
            bookDao.updateDownloadStatus(
                id = book.id,
                isDownloaded = true,
                pdfPath = file.absolutePath,
                fileSizeBytes = size
            )
            Result.success(size)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteDownload(book: BookEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            pdfHelper.deleteLocalDownload(book)
            bookDao.updateDownloadStatus(
                id = book.id,
                isDownloaded = false,
                pdfPath = "",
                fileSizeBytes = 0L
            )
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun downloadAllBooks(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val books = allBooks.firstOrNull() ?: emptyList()
            var count = 0
            for (book in books) {
                if (!book.isDownloaded) {
                    val (file, size) = pdfHelper.downloadBookToStorage(book)
                    bookDao.updateDownloadStatus(book.id, true, file.absolutePath, size)
                    count++
                }
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllDownloads(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val books = allBooks.firstOrNull() ?: emptyList()
            var count = 0
            for (book in books) {
                if (book.isDownloaded) {
                    pdfHelper.deleteLocalDownload(book)
                    bookDao.updateDownloadStatus(book.id, false, "", 0L)
                    count++
                }
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBook(book: BookEntity) = withContext(Dispatchers.IO) {
        pdfHelper.deleteLocalDownload(book)
        bookNoteDao.deleteNotesByBookId(book.id)
        bookDao.deleteBook(book)
    }

    suspend fun deleteBookById(id: String) = withContext(Dispatchers.IO) {
        val book = bookDao.getBookByIdSync(id)
        if (book != null) {
            pdfHelper.deleteLocalDownload(book)
        }
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

    // Salón de los Ídolos (SQLite)
    fun getIdolById(id: String): Flow<IdolEntity?> {
        return idolDao.getIdolById(id)
    }

    suspend fun getIdolByIdSync(id: String): IdolEntity? = withContext(Dispatchers.IO) {
        idolDao.getIdolByIdSync(id)
    }

    suspend fun insertIdol(idol: IdolEntity) = withContext(Dispatchers.IO) {
        idolDao.insertIdol(idol)
    }

    suspend fun updateIdol(idol: IdolEntity) = withContext(Dispatchers.IO) {
        idolDao.updateIdol(idol)
    }

    suspend fun deleteIdol(idol: IdolEntity) = withContext(Dispatchers.IO) {
        idolDao.deleteIdol(idol)
    }

    suspend fun deleteIdolById(id: String) = withContext(Dispatchers.IO) {
        idolDao.deleteIdolById(id)
    }

    // Import / Export JSON & Remote Repositories / Google Drive
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

    suspend fun importFromUrl(url: String, replaceExisting: Boolean = false): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val parsed = BooksJsonManager.fetchCatalogFromUrl(url)
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
        val currentBooks = allBooks.firstOrNull() ?: emptyList()
        val currentNotes = allNotes.firstOrNull() ?: emptyList()
        BooksJsonManager.exportToJsonString(currentBooks, currentNotes)
    }

    suspend fun loadDemoCatalog() = withContext(Dispatchers.IO) {
        val initialData = BooksJsonManager.loadInitialCatalog(context)
        if (initialData != null) {
            bookDao.insertBooks(initialData.books)
            bookNoteDao.insertNotes(initialData.notes)
        }
        idolDao.insertIdols(IdolsDataSource.IDOLS)
    }
}
