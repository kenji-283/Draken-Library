package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.BookCategory
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.IdolAuthor
import com.example.data.repository.LibraryRepository
import com.example.pdf.PdfHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class LibraryUiState(
    val books: List<BookEntity> = emptyList(),
    val filteredBooks: List<BookEntity> = emptyList(),
    val selectedCategory: String = "Todos",
    val searchQuery: String = "",
    val activeBook: BookEntity? = null,
    val activeBookNotes: List<BookNoteEntity> = emptyList(),
    val allNotes: List<BookNoteEntity> = emptyList(),
    val idols: List<IdolAuthor> = emptyList(),
    val selectedIdol: IdolAuthor? = null,
    val isSeeding: Boolean = false,
    val syncMessage: String? = null
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = LibraryRepository(db.bookDao(), db.bookNoteDao(), application)
    val pdfHelper = PdfHelper(application)

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeBookId = MutableStateFlow<String?>(null)
    private val _activeBook = MutableStateFlow<BookEntity?>(null)
    val activeBook: StateFlow<BookEntity?> = _activeBook.asStateFlow()

    private val _activeBookNotes = MutableStateFlow<List<BookNoteEntity>>(emptyList())
    val activeBookNotes: StateFlow<List<BookNoteEntity>> = _activeBookNotes.asStateFlow()

    private val _selectedIdol = MutableStateFlow<IdolAuthor?>(null)
    val selectedIdol: StateFlow<IdolAuthor?> = _selectedIdol.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    val idols: List<IdolAuthor> = repository.getAllIdols()

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.allBooks,
        _selectedCategory,
        _searchQuery,
        _activeBook,
        _activeBookNotes,
        repository.allNotes,
        _selectedIdol,
        _syncMessage
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val books = args[0] as List<BookEntity>
        val category = args[1] as String
        val query = args[2] as String
        val activeBook = args[3] as BookEntity?
        @Suppress("UNCHECKED_CAST")
        val activeNotes = args[4] as List<BookNoteEntity>
        @Suppress("UNCHECKED_CAST")
        val allNotes = args[5] as List<BookNoteEntity>
        val selectedIdol = args[6] as IdolAuthor?
        val syncMsg = args[7] as String?

        val filtered = books.filter { book ->
            val matchesCategory = if (category == "Todos") true else book.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) true else {
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true) ||
                book.category.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }

        LibraryUiState(
            books = books,
            filteredBooks = filtered,
            selectedCategory = category,
            searchQuery = query,
            activeBook = activeBook,
            activeBookNotes = activeNotes,
            allNotes = allNotes,
            idols = idols,
            selectedIdol = selectedIdol,
            syncMessage = syncMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState(idols = idols)
    )

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectBook(bookId: String) {
        _activeBookId.value = bookId
        viewModelScope.launch {
            repository.getBookById(bookId).collect { book ->
                _activeBook.value = book
            }
        }
        viewModelScope.launch {
            repository.getNotesForBook(bookId).collect { notes ->
                _activeBookNotes.value = notes
            }
        }
    }

    fun clearActiveBook() {
        _activeBookId.value = null
        _activeBook.value = null
        _activeBookNotes.value = emptyList()
    }

    fun updateBookRating(bookId: String, rating: Int) {
        viewModelScope.launch {
            repository.updateRating(bookId, rating)
        }
    }

    fun updateReadingProgress(bookId: String, page: Int, totalPages: Int) {
        viewModelScope.launch {
            val isFinished = page >= totalPages
            repository.updateProgress(bookId, page, isFinished)
        }
    }

    fun addBook(
        title: String,
        author: String,
        category: String,
        rating: Int,
        synopsis: String,
        totalPages: Int,
        pdfPath: String = ""
    ) {
        viewModelScope.launch {
            val newBook = BookEntity(
                title = title.trim(),
                author = author.trim(),
                category = category,
                rating = rating.coerceIn(1, 5),
                synopsis = synopsis.trim(),
                totalPages = totalPages.coerceAtLeast(1),
                currentPage = 1,
                isFinished = false,
                pdfPath = pdfPath,
                dateAdded = System.currentTimeMillis()
            )
            repository.insertBook(newBook)
            _syncMessage.value = "Libro '${newBook.title}' añadido a la biblioteca."
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
            if (_activeBookId.value == book.id) {
                clearActiveBook()
            }
            _syncMessage.value = "Libro eliminado del catálogo."
        }
    }

    fun addNote(
        bookId: String,
        pageNumber: Int,
        selectedText: String,
        noteText: String,
        highlightColorHex: String = "#BB86FC"
    ) {
        viewModelScope.launch {
            val note = BookNoteEntity(
                bookId = bookId,
                pageNumber = pageNumber,
                selectedText = selectedText.trim(),
                noteText = noteText.trim(),
                highlightColorHex = highlightColorHex,
                timestamp = System.currentTimeMillis()
            )
            repository.insertNote(note)
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNoteById(noteId)
        }
    }

    fun selectIdol(idolId: String) {
        _selectedIdol.value = repository.getIdolById(idolId)
    }

    fun clearSelectedIdol() {
        _selectedIdol.value = null
    }

    fun importJson(jsonString: String, replaceExisting: Boolean) {
        viewModelScope.launch {
            val result = repository.importJsonData(jsonString, replaceExisting)
            result.onSuccess { count ->
                _syncMessage.value = "Sincronización completada: $count libros procesados con éxito."
            }.onFailure { error ->
                _syncMessage.value = "Error al sincronizar JSON: ${error.localizedMessage}"
            }
        }
    }

    fun exportDatabaseJson(onExported: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportCurrentDatabase()
            onExported(json)
        }
    }

    fun resetCatalogToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaultCatalog()
            _syncMessage.value = "Catálogo restaurado a valores iniciales."
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}
