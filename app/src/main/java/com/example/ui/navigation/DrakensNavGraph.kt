package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.DrakensBottomBar
import com.example.ui.screens.AddBookDialog
import com.example.ui.screens.AddIdolDialog
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.DriveSyncDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IdolDetailScreen
import com.example.ui.screens.IdolsScreen
import com.example.ui.screens.PdfReaderScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CharcoalBlack
import com.example.ui.viewmodel.LibraryViewModel

object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val BOOK_DETAIL = "book_detail/{bookId}"
    const val PDF_READER = "pdf_reader/{bookId}"
    const val IDOLS = "idols"
    const val IDOL_DETAIL = "idol_detail/{idolId}"

    fun bookDetail(bookId: String) = "book_detail/$bookId"
    fun pdfReader(bookId: String) = "pdf_reader/$bookId"
    fun idolDetail(idolId: String) = "idol_detail/$idolId"
}

@Composable
fun DrakensNavGraph(
    viewModel: LibraryViewModel,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddBookDialog by remember { mutableStateOf(false) }
    var showAddIdolDialog by remember { mutableStateOf(false) }
    var showDriveSyncDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.syncMessage) {
        uiState.syncMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSyncMessage()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.SPLASH

    val showBottomBar = currentRoute in listOf(NavRoutes.HOME, NavRoutes.CATALOG, NavRoutes.IDOLS)

    Scaffold(
        containerColor = CharcoalBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                DrakensBottomBar(
                    currentRoute = currentRoute,
                    onNavigateHome = {
                        if (currentRoute != NavRoutes.HOME) {
                            navController.navigate(NavRoutes.HOME) {
                                popUpTo(NavRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateCatalog = {
                        if (currentRoute != NavRoutes.CATALOG) {
                            navController.navigate(NavRoutes.CATALOG) {
                                popUpTo(NavRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateIdols = {
                        if (currentRoute != NavRoutes.IDOLS) {
                            navController.navigate(NavRoutes.IDOLS) {
                                popUpTo(NavRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateSyncOrNotes = {
                        showDriveSyncDialog = true
                    }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 0. Splash Screen con Emblema y Nombre
            composable(NavRoutes.SPLASH) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.SPLASH) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 1. Portada / Menú Principal
            composable(NavRoutes.HOME) {
                HomeScreen(
                    uiState = uiState,
                    onNavigateToCatalog = { navController.navigate(NavRoutes.CATALOG) },
                    onNavigateToIdols = { navController.navigate(NavRoutes.IDOLS) },
                    onBookClick = { bookId ->
                        viewModel.selectBook(bookId)
                        navController.navigate(NavRoutes.bookDetail(bookId))
                    },
                    onOpenPdfReader = { book ->
                        viewModel.selectBook(book.id)
                        navController.navigate(NavRoutes.pdfReader(book.id))
                    },
                    onOpenSyncDialog = { showDriveSyncDialog = true },
                    onDownloadBook = { book -> viewModel.downloadBook(book) },
                    onDeleteDownload = { book -> viewModel.deleteDownload(book) }
                )
            }

            // 2. Catálogo de Lecturas
            composable(NavRoutes.CATALOG) {
                CatalogScreen(
                    uiState = uiState,
                    onCategorySelected = { category -> viewModel.selectCategory(category) },
                    onSearchQueryChanged = { query -> viewModel.updateSearchQuery(query) },
                    onBookClick = { bookId ->
                        viewModel.selectBook(bookId)
                        navController.navigate(NavRoutes.bookDetail(bookId))
                    },
                    onAddBookClick = { showAddBookDialog = true },
                    onBackClick = { navController.popBackStack() },
                    onDownloadBook = { book -> viewModel.downloadBook(book) },
                    onDeleteDownload = { book -> viewModel.deleteDownload(book) }
                )
            }

            // 3. Detalle de Libro
            composable(
                route = NavRoutes.BOOK_DETAIL,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val activeBook = uiState.books.find { it.id == bookId } ?: uiState.activeBook
                val bookNotes = uiState.allNotes.filter { it.bookId == bookId }
                val isDownloading = uiState.downloadingBookIds.contains(bookId)

                BookDetailScreen(
                    book = activeBook,
                    notes = bookNotes,
                    isDownloading = isDownloading,
                    onBackClick = { navController.popBackStack() },
                    onRatingChanged = { newRating ->
                        if (activeBook != null) {
                            viewModel.updateBookRating(activeBook.id, newRating)
                        }
                    },
                    onOpenPdfReader = {
                        if (activeBook != null) {
                            navController.navigate(NavRoutes.pdfReader(activeBook.id))
                        }
                    },
                    onAddNote = { page, quote, note, colorHex ->
                        if (activeBook != null) {
                            viewModel.addNote(activeBook.id, page, quote, note, colorHex)
                        }
                    },
                    onDeleteNote = { noteId ->
                        viewModel.deleteNote(noteId)
                    },
                    onDeleteBook = { bookToDelete ->
                        viewModel.deleteBook(bookToDelete)
                    },
                    onDownloadBook = { book -> viewModel.downloadBook(book) },
                    onDeleteDownload = { book -> viewModel.deleteDownload(book) }
                )
            }

            // 4. Lector PDF Integrado con Subrayado y Anotaciones
            composable(
                route = NavRoutes.PDF_READER,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val activeBook = uiState.books.find { it.id == bookId } ?: uiState.activeBook
                val bookNotes = uiState.allNotes.filter { it.bookId == bookId }

                PdfReaderScreen(
                    book = activeBook,
                    notes = bookNotes,
                    pdfHelper = viewModel.pdfHelper,
                    onBackClick = { navController.popBackStack() },
                    onProgressChanged = { page, total ->
                        if (activeBook != null) {
                            viewModel.updateReadingProgress(activeBook.id, page, total)
                        }
                    },
                    onAddNote = { page, quote, note, colorHex ->
                        if (activeBook != null) {
                            viewModel.addNote(activeBook.id, page, quote, note, colorHex)
                        }
                    },
                    onDeleteNote = { noteId ->
                        viewModel.deleteNote(noteId)
                    }
                )
            }

            // 5. Salón de los Ídolos
            composable(NavRoutes.IDOLS) {
                IdolsScreen(
                    idols = uiState.idols,
                    onIdolClick = { idolId ->
                        viewModel.selectIdol(idolId)
                        navController.navigate(NavRoutes.idolDetail(idolId))
                    },
                    onAddIdolClick = { showAddIdolDialog = true },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 6. Detalle del Ídolo (Autor)
            composable(
                route = NavRoutes.IDOL_DETAIL,
                arguments = listOf(navArgument("idolId") { type = NavType.StringType })
            ) { backStackEntry ->
                val idolId = backStackEntry.arguments?.getString("idolId") ?: ""
                val selectedIdol = uiState.idols.find { it.id == idolId } ?: uiState.selectedIdol

                IdolDetailScreen(
                    idol = selectedIdol,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = { idol -> viewModel.deleteIdol(idol) }
                )
            }
        }
    }

    // Modal para Añadir Libro
    if (showAddBookDialog) {
        AddBookDialog(
            onDismiss = { showAddBookDialog = false },
            onConfirm = { title, author, category, rating, synopsis, totalPages, pdfPath ->
                viewModel.addBook(title, author, category, rating, synopsis, totalPages, pdfPath)
                showAddBookDialog = false
            }
        )
    }

    // Modal para Añadir Ídolo
    if (showAddIdolDialog) {
        AddIdolDialog(
            onDismiss = { showAddIdolDialog = false },
            onConfirm = { nombre, corriente, epoca, fraseCelebre, porqueMeEncanto, biografia, obrasPrincipales, rutaFoto ->
                viewModel.addIdol(
                    nombre = nombre,
                    corriente = corriente,
                    epoca = epoca,
                    fraseCelebre = fraseCelebre,
                    porqueMeEncanto = porqueMeEncanto,
                    biografia = biografia,
                    obrasPrincipales = obrasPrincipales,
                    rutaFoto = rutaFoto
                )
                showAddIdolDialog = false
            }
        )
    }

    // Modal de Sincronización Google Drive / Almacenamiento & Repositorio
    if (showDriveSyncDialog) {
        DriveSyncDialog(
            totalStorageBytes = uiState.totalStorageBytes,
            downloadedCount = uiState.downloadedBooks.size,
            totalBooksCount = uiState.books.size,
            onDismiss = { showDriveSyncDialog = false },
            onImportJson = { json, replace ->
                viewModel.importJson(json, replace)
            },
            onImportFromUrl = { url, replace ->
                viewModel.importFromDriveOrUrl(url, replace)
            },
            onExportRequested = { callback ->
                viewModel.exportDatabaseJson(callback)
            },
            onDownloadAll = {
                viewModel.downloadAllBooks()
            },
            onDeleteAllDownloads = {
                viewModel.deleteAllDownloads()
            },
            onResetDefaults = {
                viewModel.resetCatalogToDefaults()
            }
        )
    }
}
