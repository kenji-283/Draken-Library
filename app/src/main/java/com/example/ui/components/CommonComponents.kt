package com.example.ui.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookEntity
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

@Composable
fun StarRatingBar(
    rating: Int,
    maxRating: Int = 5,
    onRatingChanged: ((Int) -> Unit)? = null,
    starSize: Dp = 18.dp,
    starColor: Color = AmberStar,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..maxRating) {
            val isFilled = i <= rating
            val icon = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder
            val iconTint = if (isFilled) starColor else MediumGray.copy(alpha = 0.4f)

            if (onRatingChanged != null) {
                IconButton(
                    onClick = { onRatingChanged(i) },
                    modifier = Modifier
                        .size(starSize + 12.dp)
                        .testTag("star_rating_$i")
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Calificar $i estrellas",
                        tint = iconTint,
                        modifier = Modifier.size(starSize)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = "$rating de $maxRating estrellas",
                    tint = iconTint,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) NightViolet else DarkSurface
    val textColor = if (isSelected) CharcoalBlack else MediumGray
    val borderColor = if (isSelected) NightViolet else SubtleDivider

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
            .height(36.dp)
            .testTag("category_chip_$category")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            Text(
                text = category,
                color = textColor,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun CategoryTag(
    category: String,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (category.lowercase()) {
        "filosofía" -> Color(0xFFBB86FC)
        "ciencia" -> Color(0xFF03DAC6)
        "novela" -> Color(0xFFFFB703)
        "poesía" -> Color(0xFFFF70A6)
        "teatro" -> Color(0xFF70D6FF)
        else -> NightViolet
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurfaceActive)
            .border(1.dp, SubtleDivider, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = category.uppercase(),
            color = categoryColor,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun BookCoverVisual(
    title: String,
    author: String,
    category: String,
    modifier: Modifier = Modifier
) {
    val gradientColors = when (category.lowercase()) {
        "filosofía" -> listOf(Color(0xFF2C1338), Color(0xFF1E1E1E), Color(0xFF121212))
        "ciencia" -> listOf(Color(0xFF0D2826), Color(0xFF1E1E1E), Color(0xFF121212))
        "novela" -> listOf(Color(0xFF332008), Color(0xFF1E1E1E), Color(0xFF121212))
        "poesía" -> listOf(Color(0xFF331122), Color(0xFF1E1E1E), Color(0xFF121212))
        "teatro" -> listOf(Color(0xFF102333), Color(0xFF1E1E1E), Color(0xFF121212))
        else -> listOf(NightVioletDark, DarkSurface, CharcoalBlack)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.verticalGradient(gradientColors))
            .border(1.dp, SubtleDivider, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        // Spine accent line
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxSize()
                .background(NightViolet.copy(alpha = 0.6f))
                .align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                CategoryTag(category = category)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    color = SmokeWhite,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
            }

            Column {
                Text(
                    text = author,
                    color = MediumGray,
                    fontSize = 10.5.sp,
                    fontStyle = FontStyle.Italic,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title.uppercase(),
                color = MediumGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MediumGray.copy(alpha = 0.8f),
                    fontSize = 11.5.sp
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                color = NightViolet,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onActionClick() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun IdolAvatarCircle(
    initial: String,
    name: String,
    isActive: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(72.dp)
            .clickable { onClick() }
            .testTag("idol_avatar_$name")
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isActive) AvatarGradient else Brush.linearGradient(listOf(SubtleDivider, SubtleDivider)))
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
                Text(
                    text = initial.take(1).uppercase(),
                    color = if (isActive) SmokeWhite else MediumGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            color = if (isActive) SmokeWhite else MediumGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DrakensBottomBar(
    currentRoute: String,
    onNavigateHome: () -> Unit,
    onNavigateCatalog: () -> Unit,
    onNavigateIdols: () -> Unit,
    onNavigateSyncOrNotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SubtleDivider),
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                label = "Inicio",
                icon = Icons.Default.Home,
                isSelected = currentRoute == "home",
                onClick = onNavigateHome,
                testTag = "bottom_nav_home"
            )
            BottomBarItem(
                label = "Catálogo",
                icon = Icons.Default.MenuBook,
                isSelected = currentRoute == "catalog",
                onClick = onNavigateCatalog,
                testTag = "bottom_nav_catalog"
            )
            BottomBarItem(
                label = "Ídolos",
                icon = Icons.Default.Psychology,
                isSelected = currentRoute == "idols",
                onClick = onNavigateIdols,
                testTag = "bottom_nav_idols"
            )
            BottomBarItem(
                label = "Notas",
                icon = Icons.Default.EditNote,
                isSelected = currentRoute == "notes",
                onClick = onNavigateSyncOrNotes,
                testTag = "bottom_nav_notes"
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val tint = if (isSelected) NightViolet else MediumGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

