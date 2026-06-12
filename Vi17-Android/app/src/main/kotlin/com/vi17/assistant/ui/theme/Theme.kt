package com.vi17.assistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0a7ea4),
    secondary = Color(0xFF03dac6),
    tertiary = Color(0xFFff0266),
    background = Color(0xFF151718),
    surface = Color(0xFF1e2022),
    onBackground = Color(0xFFECEDEE),
    onSurface = Color(0xFFECEDEE)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0a7ea4),
    secondary = Color(0xFF03dac6),
    tertiary = Color(0xFFff0266),
    background = Color(0xFFffffff),
    surface = Color(0xFFf5f5f5),
    onBackground = Color(0xFF11181C),
    onSurface = Color(0xFF11181C)
)

@Composable
fun ViAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
