package com.example.studyapp.core.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = LevelColor,
    tertiary = ExpColor,
    background = DarkSurfaceBg,
    surface = DarkCardBg,
    onBackground = DarkTextMain,
    onSurface = DarkTextMain,
    onSurfaceVariant = DarkTextSub,
    outline = DarkCardBorder,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = LevelColor,
    tertiary = ExpColor,
    background = LightSurfaceBg,
    surface = LightCardBg,
    onBackground = LightTextMain,
    onSurface = LightTextMain,
    onSurfaceVariant = LightTextSub,
    outline = LightCardBorder,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark
)

@Composable
fun StudyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to keep our brand colors
    dynamicColor: Boolean = false,
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

// Helpers para acceder a los colores de forma semántica
object StudyTheme {
    val colorScheme: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme

    val surfaceBg @Composable get() = colorScheme.background
    val cardBg @Composable get() = colorScheme.surface
    val cardBorder @Composable get() = colorScheme.outline
    val textMain @Composable get() = colorScheme.onBackground
    val textSub @Composable get() = colorScheme.onSurfaceVariant
    
    // Perfil adaptativo
    val profileBg @Composable get() = if (isSystemInDarkTheme()) DarkProfileBg else LightProfileBg
    val profileBorder @Composable get() = if (isSystemInDarkTheme()) DarkProfileBorder else LightProfileBorder

    // Adaptativos manuales
    val topicPillBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFDBEAFE)
    val topicPillText @Composable get() = if (isSystemInDarkTheme()) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
}
