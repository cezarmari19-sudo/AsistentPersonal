package com.asistent.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary          = Teal,
    onPrimary        = Background,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF071F18),
    background       = Background,
    surface          = Surface,
    surfaceVariant   = SurfaceVar,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    onSurfaceVariant = TextSecond,
    outline          = Border,
    error            = Error,
)

@Composable
fun AsistentTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = Typography, content = content)
}
