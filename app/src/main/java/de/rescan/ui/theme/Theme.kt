package de.rescan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.material.*

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

private val Shapes = Shapes()

@Composable
fun RescanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

}