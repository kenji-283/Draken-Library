package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import com.example.data.model.IdolEntity
import com.example.data.repository.LibraryRepository
import com.example.pdf.PdfHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LibraryUiState(
    val books: List<BookEntity> = emptyList(),
    val filteredBooks: List<BookEntity> = emptyList(),
    val downloadedBooks: List<BookEntity> = emptyList(),
    val selectedCategory: String = "Todos",
    val searchQuery: String = "",
    val activeBook: BookEntity? = null,
    val activeBookNotes: List<BookNoteEntity> = emptyList(),
    val allNotes: List<BookNoteEntity> = emptyList(),
    val idols: List<IdolEntity> = emptyList(),
    val selectedIdol: IdolEntity? = null,
    val totalStorageBytes: Long = 0L,
    val downloadingBookIds: Set<String> = emptySet(),
    val isSeeding: Boolean = false,
    val syncMessage: String? = null
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = LibraryRepository(db.bookDao(), db.bookNoteDao(), db.idolDao(), application)
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

    private val _selectedIdolId = MutableStateFlow<String?>(null)
    private val _selectedIdol = MutableStateFlow<IdolEntity?>(null)
    val selectedIdol: StateFlow<IdolEntity?> = _selectedIdol.asStateFlow()

    private val _downloadingBookIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingBookIds: StateFlow<Set<String>> = _downloadingBookIds.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.allBooks,
        _selectedCategory,
        _searchQuery,
        _activeBook,
        _activeBookNotes,
        repository.allNotes,
        repository.allIdols,
        _selectedIdol,
        _syncMessage,
        _downloadingBookIds
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val books = (args[0] as? List<BookEntity>) ?: emptyList()
        val category = (args[1] as? String) ?: "Todos"
        val query = (args[2] as? String) ?: ""
        val activeBook = args[3] as? BookEntity
        @Suppress("UNCHECKED_CAST")
        val activeNotes = (args[4] as? List<BookNoteEntity>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val allNotes = (args[5] as? List<BookNoteEntity>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val idols = (args[6] as? List<IdolEntity>) ?: emptyList()
        val selectedIdol = args[7] as? IdolEntity
        val syncMsg = args[8] as? String
        @Suppress("UNCHECKED_CAST")
        val downloadingIds = (args[9] as? Set<String>) ?: emptySet()

        val downloadedList = books.filter { it.isDownloaded }

        val filtered = books.filter { book ->
            val matchesCategory = when (category) {
                "Todos" -> true
                "Descargados" -> book.isDownloaded
                "En Línea" -> !book.isDownloaded
                else -> book.category.equals(category, ignoreCase = true)
            }
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
            downloadedBooks = downloadedList,
            selectedCategory = category,
            searchQuery = query,
            activeBook = activeBook,
            activeBookNotes = activeNotes,
            allNotes = allNotes,
            idols = idols,
            selectedIdol = selectedIdol,
            totalStorageBytes = downloadedList.sumOf { it.fileSizeBytes },
            downloadingBookIds = downloadingIds,
            syncMessage = syncMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LibraryUiState()
    )

    init {
        viewModelScope.launch {
            try {
                repository.checkAndSeedInitialData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    fun downloadBook(book: BookEntity) {
        viewModelScope.launch {
            _downloadingBookIds.value = _downloadingBookIds.value + book.id
            val result = repository.downloadBook(book)
            _downloadingBookIds.value = _downloadingBookIds.value - book.id
            result.onSuccess { size ->
                val sizeMb = String.format("%.1f", size / (1024.0 * 1024.0))
                _syncMessage.value = "'${book.title}' descargado para lectura offline ($sizeMb MB)."
            }.onFailure { error ->
                _syncMessage.value = "Error al descargar '${book.title}': ${error.localizedMessage}"
            }
        }
    }

    fun deleteDownload(book: BookEntity) {
        viewModelScope.launch {
            val result = repository.deleteDownload(book)
            result.onSuccess {
                _syncMessage.value = "Descarga eliminada. Espacio liberado para '${book.title}'."
            }.onFailure { error ->
                _syncMessage.value = "Error al liberar espacio: ${error.localizedMessage}"
            }
        }
    }

    fun downloadAllBooks() {
        viewModelScope.launch {
            _syncMessage.value = "Descargando libros para modo offline..."
            val result = repository.downloadAllBooks()
            result.onSuccess { count ->
                _syncMessage.value = "Se descargaron $count libros al almacenamiento local."
            }.onFailure { error ->
                _syncMessage.value = "Error al descargar biblioteca: ${error.localizedMessage}"
            }
        }
    }

    fun deleteAllDownloads() {
        viewModelScope.launch {
            val result = repository.deleteAllDownloads()
            result.onSuccess { count ->
                _syncMessage.value = "Se liberó el espacio de $count libros descargados."
            }.onFailure { error ->
                _syncMessage.value = "Error al liberar espacio: ${error.localizedMessage}"
            }
        }
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
        pdfPath: String = "",
        pdfOnlineUrl: String = ""
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
                pdfOnlineUrl = pdfOnlineUrl.trim(),
                isDownloaded = pdfPath.isNotEmpty(),
                dateAdded = System.currentTimeMillis()
            )
            repository.insertBook(newBook)
            _syncMessage.value = "Libro '${newBook.title}' registrado en la base de datos."
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

    // Salón de los Ídolos CRUD
    fun selectIdol(idolId: String) {
        _selectedIdolId.value = idolId
        viewModelScope.launch {
            repository.getIdolById(idolId).collect { idol ->
                _selectedIdol.value = idol
            }
        }
    }

    fun clearSelectedIdol() {
        _selectedIdolId.value = null
        _selectedIdol.value = null
    }

    fun addIdol(
        nombre: String,
        rutaFoto: String,
        biografia: String,
        porqueMeEncanto: String,
        epoca: String = "",
        corriente: String = "",
        fraseCelebre: String = "",
        obrasPrincipales: String = ""
    ) {
        viewModelScope.launch {
            val newIdol = IdolEntity(
                nombre = nombre.trim(),
                rutaFoto = rutaFoto,
                biografia = biografia.trim(),
                porqueMeEncanto = porqueMeEncanto.trim(),
                epoca = epoca.trim(),
                corriente = corriente.trim(),
                fraseCelebre = fraseCelebre.trim(),
                obrasPrincipales = obrasPrincipales.trim()
            )
            repository.insertIdol(newIdol)
            _syncMessage.value = "Autor '${newIdol.nombre}' añadido al Salón de los Ídolos."
        }
    }

    fun deleteIdol(idol: IdolEntity) {
        viewModelScope.launch {
            repository.deleteIdol(idol)
            if (_selectedIdolId.value == idol.id) {
                clearSelectedIdol()
            }
            _syncMessage.value = "Autor eliminado del Salón de los Ídolos."
        }
    }

    fun deleteIdolById(id: String) {
        viewModelScope.launch {
            repository.deleteIdolById(id)
            if (_selectedIdolId.value == id) {
                clearSelectedIdol()
            }
        }
    }

    fun importJson(jsonString: String, replaceExisting: Boolean) {
        viewModelScope.launch {
            val result = repository.importJsonData(jsonString, replaceExisting)
            result.onSuccess { count ->
                _syncMessage.value = "Importación exitosa: $count libros registrados en SQLite."
            }.onFailure { error ->
                _syncMessage.value = "Error al importar JSON: ${error.localizedMessage}"
            }
        }
    }

    fun importFromDriveOrUrl(url: String, replaceExisting: Boolean) {
        viewModelScope.launch {
            _syncMessage.value = "Conectando con repositorio / Google Drive..."
            val result = repository.importFromUrl(url, replaceExisting)
            result.onSuccess { count ->
                _syncMessage.value = "Sincronización completada: $count libros importados."
            }.onFailure { error ->
                _syncMessage.value = "Error al conectar con el repositorio: ${error.localizedMessage}"
            }
        }
    }

    fun exportDatabaseJson(onExported: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportCurrentDatabase()
            onExported(json)
        }
    }

    fun loadDemoCatalog() {
        viewModelScope.launch {
            repository.loadDemoCatalog()
            _syncMessage.value = "Catálogo clásico de ejemplo cargado en SQLite."
        }
    }

    fun resetCatalogToDefaults() {
        viewModelScope.launch {
            repository.loadDemoCatalog()
            _syncMessage.value = "Catálogo clásico restaurado en SQLite."
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}

