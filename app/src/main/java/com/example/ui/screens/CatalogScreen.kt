package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookCategory
import com.example.data.model.BookEntity
import com.example.ui.components.BookCoverVisual
import com.example.ui.components.CategoryChip
import com.example.ui.components.CategoryTag
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.viewmodel.LibraryUiState

@Composable
fun CatalogScreen(
    uiState: LibraryUiState,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onBookClick: (String) -> Unit,
    onAddBookClick: () -> Unit,
    onBackClick: () -> Unit,
    onDownloadBook: (BookEntity) -> Unit = {},
    onDeleteDownload: (BookEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val filterTabs = remember {
        listOf("Todos", "Descargados", "En Línea") + BookCategory.ALL_CATEGORIES
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CharcoalBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBookClick,
                containerColor = NightViolet,
                contentColor = CharcoalBlack,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_book")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir nuevo libro al catálogo"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con botón de retroceso y título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("catalog_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al Menú Principal",
                        tint = SmokeWhite
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Catálogo de Lecturas",
                        color = SmokeWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.filteredBooks.size} libros (${uiState.downloadedBooks.size} descargados para offline)",
                        color = MediumGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Barra de búsqueda
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Buscar por título, autor o categoría...", color = MediumGray, fontSize = 13.5.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MediumGray
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda",
                                tint = MediumGray
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NightViolet,
                    unfocusedBorderColor = SubtleDivider,
                    focusedTextColor = SmokeWhite,
                    unfocusedTextColor = SmokeWhite,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("catalog_search_bar")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pestañas de filtrado: Todos, Descargados, En Línea, Filosofía, Ciencia, Novela, Poesía, Teatro
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_tabs_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(filterTabs) { tab ->
                    CategoryChip(
                        category = tab,
                        isSelected = uiState.selectedCategory.equals(tab, ignoreCase = true),
                        onClick = { onCategorySelected(tab) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Listado de libros
            if (uiState.filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MediumGray.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "No hay libros en este filtro",
                            color = SmokeWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Puedes cambiar de pestaña o añadir uno nuevo con el botón +",
                            color = MediumGray,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("catalog_books_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.filteredBooks, key = { it.id }) { book ->
                        val isDownloading = uiState.downloadingBookIds.contains(book.id)
                        CatalogBookItemCard(
                            book = book,
                            isDownloading = isDownloading,
                            onClick = { onBookClick(book.id) },
                            onDownloadClick = { onDownloadBook(book) },
                            onDeleteDownloadClick = { onDeleteDownload(book) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogBookItemCard(
    book: BookEntity,
    isDownloading: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onDeleteDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SubtleDivider, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("book_item_${book.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portada / Miniatura representativa
            BookCoverVisual(
                title = book.title,
                author = book.author,
                category = book.category,
                modifier = Modifier
                    .width(76.dp)
                    .height(108.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Metadatos y Calificación
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryTag(category = book.category)

                    // Estado offline / en línea
                    if (book.isDownloaded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "Descargado offline",
                                tint = Color(0xFF06D6A0),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Descargado",
                                color = Color(0xFF06D6A0),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "En Línea",
                                tint = NightViolet,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "En Línea (0 MB)",
                                color = NightViolet,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Text(
                    text = book.title,
                    color = SmokeWhite,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    color = MediumGray,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Calificación en estrellas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StarRatingBar(
                            rating = book.rating,
                            starSize = 13.dp,
                            starColor = NightViolet
                        )
                        Text(
                            text = "${book.rating}.0",
                            color = SmokeWhite,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Acciones de descarga
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = NightViolet
                        )
                    } else if (book.isDownloaded) {
                        IconButton(
                            onClick = onDeleteDownloadClick,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Liberar espacio",
                                tint = MediumGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Descargar libro",
                                tint = NightViolet,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
