package com.example.studyapp.core.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalDarkTheme = staticCompositionLocalOf { false }

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

    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
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
    val profileBg @Composable get() = if (LocalDarkTheme.current) DarkProfileBg else LightProfileBg
    val profileBorder @Composable get() = if (LocalDarkTheme.current) DarkProfileBorder else LightProfileBorder

    // Adaptativos manuales
    val topicPillBg @Composable get() = if (LocalDarkTheme.current) Color(0xFF1E293B) else Color(0xFFDBEAFE)
    val topicPillText @Composable get() = if (LocalDarkTheme.current) Color(0xFF60A5FA) else Color(0xFF1D4ED8)

    // Colores de éxito/error suavizados para modo oscuro
    val success @Composable get() = if (LocalDarkTheme.current) Color(0xFF4ADE80) else SuccessGreen
    val error @Composable get() = if (LocalDarkTheme.current) Color(0xFFF87171) else ErrorRed
    val successBg @Composable get() = if (LocalDarkTheme.current) Color(0xFF064E3B).copy(alpha = 0.4f) else LightSuccessGreen
    val errorBg @Composable get() = if (LocalDarkTheme.current) Color(0xFF450A0A).copy(alpha = 0.4f) else LightErrorRed
}
