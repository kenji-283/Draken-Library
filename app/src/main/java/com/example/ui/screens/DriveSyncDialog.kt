package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberStar
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider

@Composable
fun DriveSyncDialog(
    onDismiss: () -> Unit,
    onImportJson: (jsonString: String, replaceExisting: Boolean) -> Unit,
    onExportRequested: ((String) -> Unit) -> Unit,
    onResetDefaults: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var jsonInput by remember { mutableStateOf("") }
    var replaceExisting by remember { mutableStateOf(false) }
    var exportedJsonPreview by remember { mutableStateOf<String?>(null) }
    var showExportSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = NightViolet,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sincronización & Google Drive",
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
                Text(
                    text = "Mecanismo de sincronización manual mediante archivo estructurado 'books_database.json'. Puedes importar tus obras desde Google Drive o exportar la base de datos actual.",
                    color = MediumGray,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )

                // Botones de acción rápida: Exportar y Restaurar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onExportRequested { json ->
                                exportedJsonPreview = json
                                clipboardManager.setText(AnnotatedString(json))
                                Toast.makeText(context, "JSON copiado al portapapeles para Google Drive", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NightViolet),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NightViolet.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exportar JSON", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onResetDefaults()
                            Toast.makeText(context, "Catálogo inicial restaurado", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberStar),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberStar.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restaurar", fontSize = 12.sp)
                    }
                }

                // Importar JSON
                Text(
                    text = "IMPORTAR O SINCRONIZAR JSON:",
                    color = NightViolet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it },
                    placeholder = {
                        Text(
                            "Pega aquí el contenido de tu archivo books_database.json...",
                            color = MediumGray.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    },
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("input_sync_json")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = replaceExisting,
                        onCheckedChange = { replaceExisting = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NightViolet,
                            checkmarkColor = CharcoalBlack,
                            uncheckedColor = MediumGray
                        )
                    )
                    Text(
                        text = "Reemplazar catálogo completo existente",
                        color = SmokeWhite,
                        fontSize = 12.sp
                    )
                }

                if (exportedJsonPreview != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NightViolet.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "JSON GENERADO EXITOSAMENTE",
                                    color = NightViolet,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(exportedJsonPreview ?: ""))
                                        Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copiar",
                                        tint = SmokeWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = exportedJsonPreview!!.take(250) + "...\n(Copiado al portapapeles)",
                                color = MediumGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                maxLines = 4
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (jsonInput.isNotBlank()) {
                        onImportJson(jsonInput, replaceExisting)
                        onDismiss()
                    }
                },
                enabled = jsonInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NightViolet,
                    contentColor = CharcoalBlack
                ),
                modifier = Modifier.testTag("btn_confirm_import_json")
            ) {
                Text("Importar JSON", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MediumGray)
            }
        }
    )
}
