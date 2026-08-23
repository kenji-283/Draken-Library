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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import com.example.ui.components.BookCoverVisual
import com.example.ui.components.CategoryTag
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.AmberStar
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.NightVioletDark
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider

@Composable
fun BookDetailScreen(
    book: BookEntity?,
    notes: List<BookNoteEntity>,
    onBackClick: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onOpenPdfReader: () -> Unit,
    onAddNote: (pageNumber: Int, selectedText: String, noteText: String, colorHex: String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onDeleteBook: (BookEntity) -> Unit,
    onDownloadBook: (BookEntity) -> Unit = {},
    onDeleteDownload: (BookEntity) -> Unit = {},
    isDownloading: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (book == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CharcoalBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Cargando libro...", color = SmokeWhite)
        }
        return
    }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val sizeMb = String.format("%.1f", book.fileSizeBytes / (1024.0 * 1024.0))

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CharcoalBlack
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Barra superior de navegación
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = SmokeWhite
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("delete_book_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar de la biblioteca",
                                tint = MediumGray
                            )
                        }
                    }
                }
            }

            // Cabecera Principal del Libro
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    BookCoverVisual(
                        title = book.title,
                        author = book.author,
                        category = book.category,
                        modifier = Modifier
                            .width(110.dp)
                            .height(160.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CategoryTag(category = book.category)

                            if (book.isDownloaded) {
                                Text(
                                    text = "OFFLINE",
                                    color = Color(0xFF06D6A0),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "EN LÍNEA",
                                    color = NightViolet,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = book.title,
                            color = SmokeWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )

                        Text(
                            text = book.author,
                            color = MediumGray,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Calificación interactiva 1 a 5 estrellas
                        Column {
                            Text(
                                text = "CALIFICACIÓN PERSONAL",
                                color = MediumGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            StarRatingBar(
                                rating = book.rating,
                                maxRating = 5,
                                onRatingChanged = onRatingChanged,
                                starSize = 22.dp,
                                modifier = Modifier.testTag("detail_star_rating")
                            )
                        }
                    }
                }
            }

            // Barra de Gestión de Almacenamiento del Libro (Descargar vs En Línea)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SubtleDivider, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (book.isDownloaded) Icons.Default.DownloadDone else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = if (book.isDownloaded) Color(0xFF06D6A0) else NightViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = if (book.isDownloaded) "Descargado en dispositivo" else "Lectura en Línea (0 MB ocupados)",
                                    color = SmokeWhite,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (book.isDownloaded) "Espacio ocupado: $sizeMb MB" else "Disponible en repositorio / Google Drive",
                                    color = MediumGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = NightViolet
                            )
                        } else if (book.isDownloaded) {
                            OutlinedButton(
                                onClick = { onDeleteDownload(book) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MediumGray),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SubtleDivider),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Liberar", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = { onDownloadBook(book) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkSurfaceElevated,
                                    contentColor = NightViolet
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Descargar", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Botón de acción principal: Abrir en Lector PDF
            item {
                Button(
                    onClick = onOpenPdfReader,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NightViolet,
                        contentColor = CharcoalBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_open_pdf_reader")
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (book.isDownloaded) "LEER LIBRO (MODO OFFLINE)" else "LEER EN LÍNEA (STREAMING)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Sinopsis / Descripción
            if (book.synopsis.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SubtleDivider, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "SINOPSIS Y APUNTES DE OBRA",
                                color = NightViolet,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = book.synopsis,
                                color = SmokeWhite,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Sección: Listado de Anotaciones, Citas Textuales y Comentarios Vinculados
            item {
                SectionHeader(
                    title = "Anotaciones y Citas (${notes.size})",
                    subtitle = "Fragmentos subrayados y reflexiones",
                    actionText = "+ Añadir Cita",
                    onActionClick = { showAddNoteDialog = true }
                )
            }

            if (notes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SubtleDivider, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = MediumGray,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Sin anotaciones aún",
                                color = SmokeWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Usa el lector PDF integrado para subrayar texto o pulsa '+ Añadir Cita' para registrar tus notas.",
                                color = MediumGray,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(notes, key = { it.id }) { note ->
                    NoteItemCard(
                        note = note,
                        onDeleteClick = { onDeleteNote(note.id) }
                    )
                }
            }
        }
    }

    // Modal para Añadir Cita / Anotación Manual
    if (showAddNoteDialog) {
        AddNoteDialog(
            currentPage = book.currentPage,
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { page, quote, note, colorHex ->
                onAddNote(page, quote, note, colorHex)
                showAddNoteDialog = false
            }
        )
    }

    // Modal de confirmación para eliminar libro
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = DarkSurface,
            title = { Text("Eliminar libro", color = SmokeWhite, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar '${book.title}' y todas sus anotaciones de la base de datos?", color = MediumGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteBook(book)
                        onBackClick()
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFE63946), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar", color = SmokeWhite)
                }
            }
        )
    }
}

@Composable
private fun NoteItemCard(
    note: BookNoteEntity,
    onDeleteClick: () -> Unit
) {
    val highlightColor = try {
        Color(android.graphics.Color.parseColor(note.highlightColorHex))
    } catch (e: Exception) {
        NightViolet
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SubtleDivider, RoundedCornerShape(12.dp))
            .testTag("note_item_${note.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(highlightColor)
                    )
                    Text(
                        text = "PÁGINA ${note.pageNumber}",
                        color = highlightColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar nota",
                        tint = MediumGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fragmento textual subrayado
            if (note.selectedText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(highlightColor.copy(alpha = 0.12f))
                        .border(1.dp, highlightColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "« ${note.selectedText} »",
                        color = SmokeWhite,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 18.sp
                    )
                }
            }

            // Comentario personal
            if (note.noteText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = NightViolet,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = note.noteText,
                        color = SmokeWhite,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AddNoteDialog(
    currentPage: Int,
    onDismiss: () -> Unit,
    onConfirm: (pageNumber: Int, selectedText: String, noteText: String, colorHex: String) -> Unit
) {
    var pageText by remember { mutableStateOf(currentPage.toString()) }
    var quoteText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#BB86FC") }

    val colorOptions = listOf("#BB86FC", "#FFD166", "#06D6A0", "#EF476F")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Nueva Anotación / Cita",
                color = SmokeWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = pageText,
                    onValueChange = { pageText = it },
                    label = { Text("Número de página", color = MediumGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { quoteText = it },
                    label = { Text("Fragmento o Cita Textual", color = MediumGray) },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Comentario Personal / Reflexión", color = MediumGray) },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de color de subrayado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Color:", color = MediumGray, fontSize = 13.sp)
                    colorOptions.forEach { colorHex ->
                        val parsedColor = Color(android.graphics.Color.parseColor(colorHex))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(parsedColor)
                                .border(
                                    if (selectedColor == colorHex) 2.dp else 0.dp,
                                    if (selectedColor == colorHex) SmokeWhite else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedColor = colorHex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pageNum = pageText.toIntOrNull() ?: 1
                    onConfirm(pageNum, quoteText, commentText, selectedColor)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NightViolet,
                    contentColor = CharcoalBlack
                )
            ) {
                Text("Guardar Anotación", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MediumGray)
            }
        }
    )
}
