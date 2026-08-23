package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookCategory
import com.example.data.model.BookEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.CategoryTag
import com.example.ui.components.IdolAvatarCircle
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.AmberStar
import com.example.ui.theme.AvatarGradient
import com.example.ui.theme.CharcoalBlack
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceActive
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MediumGray
import com.example.ui.theme.NightViolet
import com.example.ui.theme.NightVioletDark
import com.example.ui.theme.SmokeWhite
import com.example.ui.theme.SubtleDivider
import com.example.ui.viewmodel.LibraryUiState

@Composable
fun HomeScreen(
    uiState: LibraryUiState,
    onNavigateToCatalog: () -> Unit,
    onNavigateToIdols: () -> Unit,
    onBookClick: (String) -> Unit,
    onOpenPdfReader: (BookEntity) -> Unit,
    onOpenSyncDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("Filosofía") }
    val categories = remember { listOf("Filosofía", "Ciencia", "Novela", "Poesía", "Teatro") }

    val recentBooks = remember(uiState.books, selectedCategoryFilter) {
        val filtered = uiState.books.filter {
            it.category.equals(selectedCategoryFilter, ignoreCase = true)
        }
        if (filtered.isNotEmpty()) filtered else uiState.books
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Immersive Header (Architecture MVVM + Title + Action Button)
        item {
            ImmersiveHeader(onOpenSyncDialog = onOpenSyncDialog)
        }

        // Horizontal Category Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    CategoryChip(
                        category = cat,
                        isSelected = selectedCategoryFilter.equals(cat, ignoreCase = true),
                        onClick = {
                            selectedCategoryFilter = cat
                        }
                    )
                }
            }
        }

        // Section: Salón de los Ídolos (Horizontal Avatar Circle Carousel)
        item {
            SectionHeader(
                title = "Salón de los Ídolos",
                actionText = "Ver todos",
                onActionClick = onNavigateToIdols
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_idols_row")
            ) {
                items(uiState.idols, key = { it.id }) { idol ->
                    IdolAvatarCircle(
                        initial = idol.nombre,
                        name = idol.nombre.split(" ").lastOrNull() ?: idol.nombre,
                        isActive = true,
                        onClick = onNavigateToIdols
                    )
                }
            }
        }

        // Section: Biblioteca Reciente
        item {
            SectionHeader(
                title = "Biblioteca Reciente",
                actionText = "Ver todos (${uiState.books.size})",
                onActionClick = onNavigateToCatalog
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                recentBooks.take(4).forEach { book ->
                    ImmersiveBookCard(
                        book = book,
                        onClick = { onBookClick(book.id) },
                        onReadClick = { onOpenPdfReader(book) }
                    )
                }
            }
        }

        // Cita de Cabecera (Pensamiento Destacado)
        item {
            QuoteOfTheDayCard()
        }

        // Métricas de la Biblioteca
        item {
            LibraryStatsCard(
                totalBooks = uiState.books.size,
                totalNotes = uiState.allNotes.size,
                totalIdols = uiState.idols.size,
                finishedBooks = uiState.books.count { it.isFinished }
            )
        }
    }
}

@Composable
private fun ImmersiveHeader(
    onOpenSyncDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ARCHITECTURE MVVM",
                color = MediumGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Draken's Library",
                color = SmokeWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }

        // Circle User / Sync Avatar Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkSurface)
                .border(1.dp, SubtleDivider, CircleShape)
                .clickable { onOpenSyncDialog() }
                .testTag("home_sync_avatar_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil y Sincronización Google Drive",
                tint = NightViolet,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ImmersiveBookCard(
    book: BookEntity,
    onClick: () -> Unit,
    onReadClick: () -> Unit
) {
    val progress = (book.currentPage.toFloat() / book.totalPages.toFloat()).coerceIn(0f, 1f)
    val percentText = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SubtleDivider, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("immersive_book_card_${book.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book cover representation with progress bar inside
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(112.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, Color(0xFF3D3D3D), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryTag(category = book.category)

                    Column {
                        // Mini progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(NightViolet.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = progress.coerceAtLeast(0.05f))
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(NightViolet)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$percentText% leído",
                            color = MediumGray,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Book Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        color = SmokeWhite,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = book.author,
                        color = MediumGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star Ratings
                    StarRatingBar(
                        rating = book.rating,
                        starSize = 13.dp,
                        starColor = NightViolet
                    )

                    // Tag PDF / Action
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CharcoalBlack)
                            .border(1.dp, SubtleDivider, RoundedCornerShape(6.dp))
                            .clickable { onReadClick() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PDF",
                            color = NightViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuoteOfTheDayCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SubtleDivider, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = NightViolet,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "PENSAMIENTO DE CABECERA",
                    color = NightViolet,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "« Tienes poder sobre tu mente, no sobre los acontecimientos externos. Date cuenta de esto y encontrarás la fuerza. »",
                color = SmokeWhite,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— Marco Aurelio, Meditaciones",
                color = MediumGray,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun LibraryStatsCard(
    totalBooks: Int,
    totalNotes: Int,
    totalIdols: Int,
    finishedBooks: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SubtleDivider, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "ESTADO DE LA BIBLIOTECA",
                color = MediumGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(count = "$totalBooks", label = "Volúmenes", icon = Icons.Default.MenuBook)
                StatItem(count = "$totalNotes", label = "Anotaciones", icon = Icons.Default.Bookmark)
                StatItem(count = "$totalIdols", label = "Ídolos", icon = Icons.Default.Psychology)
                StatItem(count = "$finishedBooks", label = "Completados", icon = Icons.Default.AutoStories)
            }
        }
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NightViolet,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count,
            color = SmokeWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = MediumGray,
            fontSize = 10.sp
        )
    }
}

