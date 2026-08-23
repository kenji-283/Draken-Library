package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.NightVioletDark
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddIdolDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        nombre: String,
        rutaFoto: String,
        biografia: String,
        porqueMeEncanto: String,
        epoca: String,
        corriente: String,
        fraseCelebre: String,
        obrasPrincipales: String
    ) -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var biografia by remember { mutableStateOf("") }
    var porqueMeEncanto by remember { mutableStateOf("") }
    var epoca by remember { mutableStateOf("") }
    var corriente by remember { mutableStateOf("") }
    var fraseCelebre by remember { mutableStateOf("") }
    var obrasPrincipales by remember { mutableStateOf("") }
    var rutaFoto by remember { mutableStateOf("") }

    // Selector de imagen de galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val idolsDir = File(context.filesDir, "idols")
                if (!idolsDir.exists()) idolsDir.mkdirs()
                val imageFile = File(idolsDir, "idol_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(imageFile).use { output ->
                        input.copyTo(output)
                    }
                }
                rutaFoto = imageFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                rutaFoto = uri.toString()
            }
        }
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = NightViolet,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Añadir Ídolo al Salón",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Selector / Vista previa de foto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                            .border(2.dp, NightViolet, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") }
                            .testTag("btn_pick_idol_photo"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rutaFoto.isNotEmpty()) {
                            AsyncImage(
                                model = rutaFoto,
                                contentDescription = "Foto seleccionada",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Elegir foto",
                                    tint = NightViolet,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = "Foto",
                                    color = MediumGray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Foto del Autor",
                            color = SmokeWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Toca el círculo para elegir una imagen desde tu galería local.",
                            color = MediumGray,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Autor / Pensador *", color = MediumGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_idol_name")
                )

                // APARTADO ESPECIAL DESTACADO: "Por qué me encantó"
                OutlinedTextField(
                    value = porqueMeEncanto,
                    onValueChange = { porqueMeEncanto = it },
                    label = { Text("¿Por qué me encantó? (Apartado Especial) *", color = NightViolet) },
                    placeholder = { Text("Explica qué hace única su filosofía o literatura y por qué te marcó...", color = MediumGray.copy(alpha = 0.5f), fontSize = 12.sp) },
                    minLines = 3,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = NightViolet.copy(alpha = 0.5f),
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_idol_why_loved")
                )

                OutlinedTextField(
                    value = biografia,
                    onValueChange = { biografia = it },
                    label = { Text("Biografía y Trayectoria *", color = MediumGray) },
                    minLines = 3,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_idol_bio")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = epoca,
                        onValueChange = { epoca = it },
                        label = { Text("Época", color = MediumGray) },
                        placeholder = { Text("Ej: 1844-1900", color = MediumGray.copy(alpha = 0.4f), fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SmokeWhite,
                            unfocusedTextColor = SmokeWhite,
                            focusedBorderColor = NightViolet,
                            unfocusedBorderColor = SubtleDivider
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = corriente,
                        onValueChange = { corriente = it },
                        label = { Text("Corriente", color = MediumGray) },
                        placeholder = { Text("Ej: Filosofía", color = MediumGray.copy(alpha = 0.4f), fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SmokeWhite,
                            unfocusedTextColor = SmokeWhite,
                            focusedBorderColor = NightViolet,
                            unfocusedBorderColor = SubtleDivider
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = fraseCelebre,
                    onValueChange = { fraseCelebre = it },
                    label = { Text("Frase Célebre o Cita", color = MediumGray) },
                    placeholder = { Text("Ej: Quien tiene un porqué para vivir...", color = MediumGray.copy(alpha = 0.4f), fontSize = 11.sp) },
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
                    value = obrasPrincipales,
                    onValueChange = { obrasPrincipales = it },
                    label = { Text("Obras Principales (separadas por comas)", color = MediumGray) },
                    placeholder = { Text("Ej: Así habló Zaratustra, El ocaso de los ídolos", color = MediumGray.copy(alpha = 0.4f), fontSize = 11.sp) },
                    singleLine = true,
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
                    if (nombre.isNotBlank() && biografia.isNotBlank() && porqueMeEncanto.isNotBlank()) {
                        onConfirm(
                            nombre.trim(),
                            rutaFoto,
                            biografia.trim(),
                            porqueMeEncanto.trim(),
                            epoca.trim(),
                            corriente.trim(),
                            fraseCelebre.trim(),
                            obrasPrincipales.trim()
                        )
                    }
                },
                enabled = nombre.isNotBlank() && biografia.isNotBlank() && porqueMeEncanto.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NightViolet,
                    contentColor = CharcoalBlack
                ),
                modifier = Modifier.testTag("btn_save_new_idol")
            ) {
                Text("Entronizar Ídolo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MediumGray)
            }
        }
    )
}
