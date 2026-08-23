package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NightViolet,
    onPrimary = CharcoalBlack,
    primaryContainer = NightVioletDark,
    onPrimaryContainer = SmokeWhite,
    secondary = NightViolet,
    onSecondary = CharcoalBlack,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = SmokeWhite,
    tertiary = AmberStar,
    onTertiary = CharcoalBlack,
    background = CharcoalBlack,
    onBackground = SmokeWhite,
    surface = DarkSurface,
    onSurface = SmokeWhite,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = MediumGray,
    outline = SubtleDivider,
    outlineVariant = SubtleDivider,
)

@Composable
fun DrakensLibraryTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

