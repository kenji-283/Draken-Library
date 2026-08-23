package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.IdolEntity
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.NightVioletDark
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider

@Composable
fun IdolsScreen(
    idols: List<IdolEntity>,
    onIdolClick: (String) -> Unit,
    onAddIdolClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CharcoalBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddIdolClick,
                containerColor = NightViolet,
                contentColor = CharcoalBlack,
                modifier = Modifier.testTag("fab_add_idol")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir Ídolo al Salón"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("idols_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = SmokeWhite
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NightViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SALÓN DE LOS ÍDOLOS",
                                    color = NightViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Autores Fundamentales",
                                color = SmokeWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onAddIdolClick,
                        modifier = Modifier.testTag("btn_header_add_idol")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir Ídolo",
                            tint = NightViolet
                        )
                    }
                }
            }

            // Introduction banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NightViolet.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "El panteón de los escritores y pensadores que han moldeado mi visión del mundo. Registra autores con su biografía, foto y la razón por la que su obra te cautivó.",
                            color = SmokeWhite,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Empty state o Lista de Ídolos
            if (idols.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceElevated)
                                .border(1.dp, SubtleDivider, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = NightViolet,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "El Salón de los Ídolos está vacío",
                            color = SmokeWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Añade a tus autores favoritos con el botón '+' para inmortalizar su biografía y legado.",
                            color = MediumGray,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onAddIdolClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NightViolet,
                                contentColor = CharcoalBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_empty_add_idol")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Añadir Primer Ídolo", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(idols, key = { it.id }) { idol ->
                    IdolAuthorCard(
                        idol = idol,
                        onClick = { onIdolClick(idol.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun IdolAuthorCard(
    idol: IdolEntity,
    onClick: () -> Unit
) {
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SubtleDivider, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("idol_card_${idol.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Retrato del autor con gradient ring
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NightViolet, NightVioletDark)))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .border(2.dp, CharcoalBlack, CircleShape),
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
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = idol.nombre,
                    color = SmokeWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (idol.corriente.isNotEmpty() || idol.epoca.isNotEmpty()) {
                    val sub = listOf(idol.corriente, idol.epoca).filter { it.isNotEmpty() }.joinToString(" • ")
                    Text(
                        text = sub,
                        color = NightViolet,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (idol.fraseCelebre.isNotEmpty()) {
                    Text(
                        text = "« ${idol.fraseCelebre} »",
                        color = MediumGray,
                        fontSize = 11.5.sp,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ver detalle",
                tint = MediumGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

