package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCoral,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A0A1C),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = CyberViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF331652),
    onSecondaryContainer = Color(0xFFECD8FF),
    tertiary = HyperCyan,
    onTertiary = Color(0xFF003737),
    tertiaryContainer = Color(0xFF004F4F),
    onTertiaryContainer = Color(0xFF70F5F2),
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    surfaceContainerHighest = CardElevated
)

private val LightColorScheme = lightColorScheme(
    primary = NeonCoral,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3E0013),
    secondary = CyberViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECD8FF),
    onSecondaryContainer = Color(0xFF260548),
    tertiary = Color(0xFF00838F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA7EDEB),
    onTertiaryContainer = Color(0xFF002023),
    background = SurfaceLight,
    onBackground = TextLightPrimary,
    surface = Color.White,
    onSurface = TextLightPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextLightSecondary,
    outline = Color(0xFFD6DBEC),
    surfaceContainerHighest = Color(0xFFE4E9F7)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek midnight theme for viral creator vibe
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
