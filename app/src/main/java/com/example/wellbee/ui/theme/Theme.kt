package com.example.wellbee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = TextDark,
    secondary = GreenAccent,
    background = GrayBackground,
    onBackground = TextDark
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = TextLight,
    secondary = GreenAccent,
    background = BlueLight,
    onBackground = TextLight
)

@Composable
fun WellbeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
