package de.rescan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors

// Create dark and light color palettes using legacy Material APIs.

private val DarkColorPalette = darkColors(
    primary = GreenDark,
    secondary = Turquoise,
    surface = BlueDark,
    background = androidx.compose.ui.graphics.Color.Black,
    onPrimary = androidx.compose.ui.graphics.Color.White
)

private val LightColorPalette = lightColors(
    primary = GreenLight,
    secondary = Turquoise,
    surface = Beige,
    background = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.Black
)

// Define default typography and shapes (using legacy Material defaults).
private val Shapes = Shapes()

@Composable
fun RescanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) { // Dynamic color support is removed because only legacy Material functions are allowed. val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

}