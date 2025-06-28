package com.example.ucmmapapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Define el esquema de colores para el tema oscuro de la aplicación.
 * Utiliza la paleta de colores definida en `Color.kt`.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Define el esquema de colores para el tema claro de la aplicación.
 * Utiliza la paleta de colores definida en `Color.kt`.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * El Composable principal que aplica el tema de la aplicación a su contenido.
 * Este composable envuelve la UI de la aplicación, proveyendo un `MaterialTheme` configurado.
 *
 * @param darkTheme Si se debe usar el tema oscuro. Por defecto, sigue la configuración del sistema.
 * @param dynamicColor Si se debe usar el color dinámico de Android 12+ (Material You).
 *                     Si está activado y el dispositivo lo soporta, los colores se basarán en el fondo de pantalla del usuario.
 * @param content El contenido de la UI al que se le aplicará el tema.
 */
@Composable
fun UCMMapAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}