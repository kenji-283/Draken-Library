package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val CharcoalBlack = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceActive = Color(0xFF252525)
val DarkSurfaceElevated = Color(0xFF262626)
val SmokeWhite = Color(0xFFE0E0E0)
val MediumGray = Color(0xFF9E9E9E)
val SubtleDivider = Color(0xFF2C2C2C)
val NightViolet = Color(0xFFBB86FC)
val NightVioletDark = Color(0xFF3700B3)
val AmberStar = Color(0xFFFFD166)
val SoftHighlightViolet = Color(0x33BB86FC)
val SoftHighlightAmber = Color(0x33FFD166)
val SoftHighlightGreen = Color(0x3306D6A0)

// Immersive Gradients
val AvatarGradient = Brush.linearGradient(
    colors = listOf(NightViolet, NightVioletDark)
)
val HeaderGradient = Brush.verticalGradient(
    colors = listOf(Color.Transparent, CharcoalBlack.copy(alpha = 0.85f), DarkSurface)
)


