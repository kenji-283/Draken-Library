package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookCategory
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.AmberStar
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        author: String,
        category: String,
        rating: Int,
        synopsis: String,
        totalPages: Int,
        pdfPath: String
    ) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(BookCategory.FILOSOFIA.displayName) }
    var rating by remember { mutableIntStateOf(5) }
    var synopsis by remember { mutableStateOf("") }
    var totalPagesText by remember { mutableStateOf("100") }
    var pdfPath by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    // Launcher para seleccionar archivo PDF del dispositivo
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val pdfDir = File(context.filesDir, "pdfs")
                if (!pdfDir.exists()) pdfDir.mkdirs()
                val targetFile = File(pdfDir, "book_${System.currentTimeMillis()}.pdf")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                pdfPath = targetFile.absolutePath
                selectedFileName = targetFile.name
            } catch (e: Exception) {
                e.printStackTrace()
                pdfPath = uri.toString()
                selectedFileName = "PDF vinculado"
            }
        }
    }

    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    val categories = BookCategory.ALL_CATEGORIES

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = NightViolet,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Añadir Libro a la Biblioteca",
                    color = SmokeWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la Obra *", color = MediumGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_book_title")
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor / Escritor *", color = MediumGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_book_author")
                )

                // Selector de Categoría Estricta (Filosofía, Ciencia, Novela, Poesía, Teatro)
                ExposedDropdownMenuBox(
                    expanded = expandedCategoryDropdown,
                    onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría Estricta *", color = MediumGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SmokeWhite,
                            unfocusedTextColor = SmokeWhite,
                            focusedBorderColor = NightViolet,
                            unfocusedBorderColor = SubtleDivider,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("dropdown_book_category")
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategoryDropdown,
                        onDismissRequest = { expandedCategoryDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category, color = SmokeWhite) },
                                onClick = {
                                    selectedCategory = category
                                    expandedCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                // Calificación en Estrellas (1 a 5)
                Column {
                    Text(
                        text = "Calificación Inicial (1 a 5 estrellas):",
                        color = MediumGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StarRatingBar(
                        rating = rating,
                        maxRating = 5,
                        onRatingChanged = { rating = it },
                        starSize = 24.dp
                    )
                }

                // Selector de Archivo PDF
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, if (pdfPath.isNotEmpty()) NightViolet else SubtleDivider, RoundedCornerShape(10.dp))
                        .clickable { pdfPickerLauncher.launch("application/pdf") }
                        .padding(12.dp)
                        .testTag("btn_pick_pdf_file")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (pdfPath.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = if (pdfPath.isNotEmpty()) NightViolet else MediumGray,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = if (pdfPath.isNotEmpty()) "Archivo PDF Cargado" else "Cargar Archivo PDF del Teléfono",
                                    color = SmokeWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (pdfPath.isNotEmpty()) (selectedFileName ?: "Listo para lectura offline") else "Toca para examinar tus documentos",
                                    color = MediumGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Examinar archivos",
                            tint = NightViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = totalPagesText,
                    onValueChange = { totalPagesText = it },
                    label = { Text("Número Total de Páginas", color = MediumGray) },
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
                    value = synopsis,
                    onValueChange = { synopsis = it },
                    label = { Text("Sinopsis / Resumen de la Obra", color = MediumGray) },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && author.isNotBlank()) {
                        val pages = totalPagesText.toIntOrNull() ?: 100
                        onConfirm(title, author, selectedCategory, rating, synopsis, pages, pdfPath)
                    }
                },
                enabled = title.isNotBlank() && author.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NightViolet,
                    contentColor = CharcoalBlack
                ),
                modifier = Modifier.testTag("btn_save_new_book")
            ) {
                Text("Registrar Obra", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MediumGray)
            }
        }
    )
}

