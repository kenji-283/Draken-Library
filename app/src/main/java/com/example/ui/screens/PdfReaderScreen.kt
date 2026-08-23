package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookEntity
import com.example.data.model.BookNoteEntity
import com.example.pdf.PdfHelper
import com.example.ui.theme.AmberStar
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.NightVioletDark
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderScreen(
    book: BookEntity?,
    notes: List<BookNoteEntity>,
    pdfHelper: PdfHelper,
    onBackClick: () -> Unit,
    onProgressChanged: (page: Int, totalPages: Int) -> Unit,
    onAddNote: (pageNumber: Int, selectedText: String, noteText: String, colorHex: String) -> Unit,
    onDeleteNote: (String) -> Unit,
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

    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var fileDescriptor by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    var pageCount by remember { mutableIntStateOf(1) }
    var currentPageIndex by remember { mutableIntStateOf((book.currentPage - 1).coerceAtLeast(0)) }
    var currentPageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingPage by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var showStudyModal by remember { mutableStateOf(false) }
    var showNotesSheet by remember { mutableStateOf(false) }

    // Zoom & Pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val coroutineScope = rememberCoroutineScope()
    val notesSheetState = rememberModalBottomSheetState()

    // Cargar el archivo PDF al iniciar
    LaunchedEffect(book.id) {
        isLoadingPage = true
        withContext(Dispatchers.IO) {
            try {
                val file = pdfHelper.getPdfFileForReading(book)
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                fileDescriptor = pfd
                pdfRenderer = renderer
                pageCount = renderer.pageCount.coerceAtLeast(1)
                currentPageIndex = (book.currentPage - 1).coerceIn(0, pageCount - 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoadingPage = false
    }

    // Renderizar página activa
    LaunchedEffect(currentPageIndex, pdfRenderer) {
        val renderer = pdfRenderer ?: return@LaunchedEffect
        if (currentPageIndex < 0 || currentPageIndex >= pageCount) return@LaunchedEffect

        isLoadingPage = true
        withContext(Dispatchers.IO) {
            try {
                val page = renderer.openPage(currentPageIndex)
                // Renderizar a 2x resolución para nitidez perfecta
                val width = page.width * 2
                val height = page.height * 2
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                currentPageBitmap = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoadingPage = false
        onProgressChanged(currentPageIndex + 1, pageCount)
    }

    // Limpiar recursos al salir
    DisposableEffect(Unit) {
        onDispose {
            try {
                pdfRenderer?.close()
                fileDescriptor?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3.5f)
        if (scale > 1f) {
            offset += offsetChange
        } else {
            offset = Offset.Zero
        }
    }

    val pageNotes = remember(notes, currentPageIndex) {
        notes.filter { it.pageNumber == currentPageIndex + 1 }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CharcoalBlack,
        topBar = {
            AnimatedVisibility(visible = showControls) {
                Surface(
                    color = DarkSurface.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.testTag("pdf_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = SmokeWhite
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = book.title,
                                    color = SmokeWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Página ${currentPageIndex + 1} de $pageCount",
                                    color = NightViolet,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Botón de herramientas de estudio / subrayado
                            IconButton(
                                onClick = { showStudyModal = true },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(NightViolet.copy(alpha = 0.15f))
                                    .testTag("btn_pdf_highlight_study")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BorderColor,
                                    contentDescription = "Subrayar y Añadir Nota",
                                    tint = NightViolet
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Botón para ver notas del libro
                            IconButton(
                                onClick = { showNotesSheet = true },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(DarkSurfaceElevated)
                                    .testTag("btn_pdf_notes_drawer")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Ver Anotaciones",
                                    tint = if (notes.isNotEmpty()) AmberStar else MediumGray
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = showControls) {
                Surface(
                    color = DarkSurface.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Slider de navegación de páginas
                        if (pageCount > 1) {
                            Slider(
                                value = (currentPageIndex + 1).toFloat(),
                                onValueChange = { newPage ->
                                    currentPageIndex = (newPage.toInt() - 1).coerceIn(0, pageCount - 1)
                                },
                                valueRange = 1f..pageCount.toFloat(),
                                steps = (pageCount - 2).coerceAtLeast(0),
                                colors = SliderDefaults.colors(
                                    thumbColor = NightViolet,
                                    activeTrackColor = NightViolet,
                                    inactiveTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Botones de página anterior y siguiente
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (currentPageIndex > 0) {
                                        currentPageIndex--
                                        scale = 1f
                                        offset = Offset.Zero
                                    }
                                },
                                enabled = currentPageIndex > 0,
                                modifier = Modifier.testTag("pdf_prev_page")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NavigateBefore,
                                    contentDescription = "Página anterior",
                                    tint = if (currentPageIndex > 0) SmokeWhite else MediumGray.copy(alpha = 0.3f)
                                )
                            }

                            // Contador y notas de la página actual
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Página ${currentPageIndex + 1} de $pageCount",
                                    color = SmokeWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (pageNotes.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(NightViolet.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${pageNotes.size} notas",
                                            color = NightViolet,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (currentPageIndex < pageCount - 1) {
                                        currentPageIndex++
                                        scale = 1f
                                        offset = Offset.Zero
                                    }
                                },
                                enabled = currentPageIndex < pageCount - 1,
                                modifier = Modifier.testTag("pdf_next_page")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = "Página siguiente",
                                    tint = if (currentPageIndex < pageCount - 1) SmokeWhite else MediumGray.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CharcoalBlack)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            showControls = !showControls
                        },
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 2f
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingPage || currentPageBitmap == null) {
                CircularProgressIndicator(
                    color = NightViolet,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                val bitmap = currentPageBitmap
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Página ${currentPageIndex + 1} de ${book.title}",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            }
                            .transformable(transformState)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }

    // Modal de Herramientas de Estudio: Subrayar fragmento y añadir nota
    if (showStudyModal) {
        StudyToolModal(
            pageNumber = currentPageIndex + 1,
            onDismiss = { showStudyModal = false },
            onSave = { quote, note, colorHex ->
                onAddNote(currentPageIndex + 1, quote, note, colorHex)
                showStudyModal = false
            }
        )
    }

    // Bottom Sheet: Anotaciones y citas de este libro
    if (showNotesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotesSheet = false },
            sheetState = notesSheetState,
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anotaciones del Libro (${notes.size})",
                        color = SmokeWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showNotesSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MediumGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (notes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No has creado anotaciones en este libro aún.",
                            color = MediumGray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notes, key = { it.id }) { note ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(note.highlightColorHex))
                            } catch (e: Exception) {
                                NightViolet
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SubtleDivider, RoundedCornerShape(10.dp))
                                    .clickable {
                                        currentPageIndex = (note.pageNumber - 1).coerceIn(0, pageCount - 1)
                                        showNotesSheet = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "PÁGINA ${note.pageNumber} • Tocar para ir",
                                            color = color,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = { onDeleteNote(note.id) },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = MediumGray,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }

                                    if (note.selectedText.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "« ${note.selectedText} »",
                                            color = SmokeWhite,
                                            fontSize = 12.5.sp,
                                            fontStyle = FontStyle.Italic,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (note.noteText.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = note.noteText,
                                            color = MediumGray,
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyToolModal(
    pageNumber: Int,
    onDismiss: () -> Unit,
    onSave: (quoteText: String, noteText: String, colorHex: String) -> Unit
) {
    var quoteText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#BB86FC") }

    val colors = listOf(
        "#BB86FC", // Púrpura nocturno
        "#FFD166", // Dorado ámbar
        "#06D6A0", // Verde esmeralda
        "#EF476F"  // Rosa rubí
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BorderColor,
                    contentDescription = null,
                    tint = NightViolet,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Subrayar & Anotar (Pág. $pageNumber)",
                    color = SmokeWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Introduce el fragmento de la página que deseas subrayar y tu comentario personal.",
                    color = MediumGray,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = quoteText,
                    onValueChange = { quoteText = it },
                    label = { Text("Fragmento o cita textual a subrayar", color = MediumGray, fontSize = 12.sp) },
                    placeholder = { Text("Ej: 'Tienes poder sobre tu mente...'", color = MediumGray.copy(alpha = 0.5f), fontSize = 12.sp) },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pdf_quote_input")
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Nota de estudio / Comentario", color = MediumGray, fontSize = 12.sp) },
                    placeholder = { Text("Añade tu análisis o reflexión...", color = MediumGray.copy(alpha = 0.5f), fontSize = 12.sp) },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pdf_note_input")
                )

                // Selector de color del resaltador
                Column {
                    Text(
                        text = "Color del resaltador:",
                        color = MediumGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colors.forEach { colorHex ->
                            val color = Color(android.graphics.Color.parseColor(colorHex))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (selectedColor == colorHex) 2.5.dp else 0.dp,
                                        if (selectedColor == colorHex) SmokeWhite else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedColor = colorHex }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (quoteText.isNotBlank() || noteText.isNotBlank()) {
                        onSave(quoteText, noteText, selectedColor)
                    }
                },
                enabled = quoteText.isNotBlank() || noteText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NightViolet,
                    contentColor = CharcoalBlack
                ),
                modifier = Modifier.testTag("btn_save_pdf_note")
            ) {
                Text("Guardar en BD", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MediumGray)
            }
        }
    )
}
