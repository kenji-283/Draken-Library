package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.IdolEntity
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
fun IdolDetailScreen(
    idol: IdolEntity?,
    onBackClick: () -> Unit,
    onDeleteClick: ((IdolEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (idol == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CharcoalBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Autor no encontrado", color = SmokeWhite)
        }
        return
    }

    val context = LocalContext.current
    val isResource = idol.rutaFoto.isNotBlank() && !idol.rutaFoto.startsWith("/") && !idol.rutaFoto.startsWith("content:")
    val imageResId = remember(idol.rutaFoto) {
        if (isResource) {
            val res = context.resources.getIdentifier(idol.rutaFoto, "drawable", context.packageName)
            if (res != 0) res else R.drawable.img_drakens_banner
        } else {
            R.drawable.img_drakens_banner
        }
    }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val obrasList = remember(idol.obrasPrincipales) {
        if (idol.obrasPrincipales.isBlank()) emptyList()
        else idol.obrasPrincipales.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    if (showDeleteConfirmDialog && onDeleteClick != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("¿Eliminar autor del Salón?", color = SmokeWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Se retirará a '${idol.nombre}' del Salón de los Ídolos permanentemente.",
                    color = MediumGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteClick(idol)
                        onBackClick()
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFCF6679), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar", color = MediumGray)
                }
            }
        )
    }

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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("idol_detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = SmokeWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Perfil del Autor",
                            color = SmokeWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (onDeleteClick != null) {
                        IconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.testTag("btn_delete_idol")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar Ídolo",
                                tint = MediumGray
                            )
                        }
                    }
                }
            }

            // Cabecera Hero con Foto y Datos Generales
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SubtleDivider, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Foto grande con halo violeta
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(2.dp, NightViolet, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (idol.rutaFoto.isNotBlank() && (idol.rutaFoto.startsWith("/") || idol.rutaFoto.startsWith("content:"))) {
                                AsyncImage(
                                    model = idol.rutaFoto,
                                    contentDescription = "Foto de ${idol.nombre}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (isResource && imageResId != 0) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = "Foto de ${idol.nombre}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = idol.nombre.take(1).uppercase(),
                                    color = SmokeWhite,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = idol.nombre,
                            color = SmokeWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (idol.corriente.isNotEmpty() || idol.epoca.isNotEmpty()) {
                            val sub = listOf(idol.corriente, idol.epoca).filter { it.isNotEmpty() }.joinToString(" • ")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sub,
                                color = NightViolet,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (idol.fraseCelebre.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurfaceElevated)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = NightViolet,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "« ${idol.fraseCelebre} »",
                                        color = SmokeWhite,
                                        fontSize = 12.5.sp,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // APARTADO ESPECIAL DESTACADO: "Por qué me encantó"
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NightViolet, RoundedCornerShape(16.dp))
                        .testTag("section_porque_me_encanto"),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkSurfaceElevated
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(NightViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = NightViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "POR QUÉ ME ENCANTÓ",
                                color = NightViolet,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = idol.porqueMeEncanto,
                            color = SmokeWhite,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Biografía Completa
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SubtleDivider, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "BIOGRAFÍA Y TRAYECTORIA",
                            color = MediumGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = idol.biografia,
                            color = SmokeWhite,
                            fontSize = 13.5.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            }

            // Obras Principales / Destacadas
            if (obrasList.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SubtleDivider, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "OBRAS DESTACADAS",
                                color = MediumGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            obrasList.forEach { obra ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = NightViolet,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = obra,
                                        color = SmokeWhite,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium
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

