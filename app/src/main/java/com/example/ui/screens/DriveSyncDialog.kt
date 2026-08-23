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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
    totalStorageBytes: Long = 0L,
    downloadedCount: Int = 0,
    totalBooksCount: Int = 0,
    onDismiss: () -> Unit,
    onImportJson: (jsonString: String, replaceExisting: Boolean) -> Unit,
    onImportFromUrl: (url: String, replaceExisting: Boolean) -> Unit,
    onExportRequested: ((String) -> Unit) -> Unit,
    onDownloadAll: () -> Unit = {},
    onDeleteAllDownloads: () -> Unit = {},
    onResetDefaults: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var remoteUrlInput by remember { mutableStateOf("") }
    var jsonInput by remember { mutableStateOf("") }
    var replaceExisting by remember { mutableStateOf(false) }
    var exportedJsonPreview by remember { mutableStateOf<String?>(null) }

    val storageMb = String.format("%.2f", totalStorageBytes / (1024.0 * 1024.0))

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
                    text = "Almacenamiento & Repositorio",
                    color = SmokeWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.5.sp
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
                // Estado del Almacenamiento
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SubtleDivider, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SdStorage,
                                contentDescription = null,
                                tint = AmberStar,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Espacio ocupado en dispositivo: $storageMb MB",
                                color = SmokeWhite,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$downloadedCount libros descargados de un total de $totalBooksCount. Los libros no descargados se transmiten en tiempo real sin ocupar espacio.",
                            color = MediumGray,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDownloadAll,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NightViolet),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NightViolet.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Descargar Todo", fontSize = 10.5.sp)
                            }

                            OutlinedButton(
                                onClick = onDeleteAllDownloads,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE63946)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE63946).copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Liberar Espacio", fontSize = 10.5.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(color = SubtleDivider, thickness = 1.dp)

                // Sincronizar desde Google Drive o Repositorio en Línea (URL)
                Text(
                    text = "IMPORTAR DESDE GOOGLE DRIVE / REPOSITORIO:",
                    color = NightViolet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                OutlinedTextField(
                    value = remoteUrlInput,
                    onValueChange = { remoteUrlInput = it },
                    placeholder = {
                        Text(
                            "Pega enlace de Google Drive o URL de repositorio...",
                            color = MediumGray.copy(alpha = 0.5f),
                            fontSize = 11.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = MediumGray)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SmokeWhite,
                        unfocusedTextColor = SmokeWhite,
                        focusedBorderColor = NightViolet,
                        unfocusedBorderColor = SubtleDivider,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (remoteUrlInput.isNotBlank()) {
                            onImportFromUrl(remoteUrlInput, replaceExisting)
                            onDismiss()
                        }
                    },
                    enabled = remoteUrlInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NightViolet,
                        contentColor = CharcoalBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sincronizar Repositorio / Drive", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = SubtleDivider, thickness = 1.dp)

                // Respaldo Manual JSON
                Text(
                    text = "COPIA DE SEGURIDAD JSON MANUAL:",
                    color = NightViolet,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onExportRequested { json ->
                                exportedJsonPreview = json
                                clipboardManager.setText(AnnotatedString(json))
                                Toast.makeText(context, "JSON copiado al portapapeles", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NightViolet),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NightViolet.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exportar JSON", fontSize = 11.5.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onResetDefaults()
                            Toast.makeText(context, "Catálogo inicial restaurado", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberStar),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberStar.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restaurar", fontSize = 11.5.sp)
                    }
                }

                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it },
                    placeholder = {
                        Text(
                            "O pega aquí texto JSON directo...",
                            color = MediumGray.copy(alpha = 0.5f),
                            fontSize = 11.5.sp
                        )
                    },
                    maxLines = 4,
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
                        .height(90.dp)
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
                        text = "Reemplazar catálogo completo",
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
                                    text = "JSON EXPORTADO CON ÉXITO",
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
                                text = exportedJsonPreview!!.take(200) + "...\n(Copiado al portapapeles)",
                                color = MediumGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (jsonInput.isNotBlank()) {
                Button(
                    onClick = {
                        onImportJson(jsonInput, replaceExisting)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NightViolet,
                        contentColor = CharcoalBlack
                    ),
                    modifier = Modifier.testTag("btn_confirm_import_json")
                ) {
                    Text("Importar JSON", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MediumGray)
            }
        }
    )
}
