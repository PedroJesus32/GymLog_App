package pt.pc.gymlog.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GymElectricBlue,
    onPrimary = Color.White,
    secondary = GymElectricBlue,
    tertiary = GymElectricBlue,
    background = GymBackgroundWhite,
    surface = GymBackgroundWhite,
    surfaceVariant = GymInputGray,
    onSurface = GymDarkText,
    onSurfaceVariant = GymDarkText,
    onBackground = GymDarkText,
    error = GymError
)

private val LightColorScheme = lightColorScheme(
    primary = GymElectricBlue,
    onPrimary = Color.White,
    secondary = GymElectricBlue,
    tertiary = GymElectricBlue,
    background = GymBackgroundWhite,
    surface = GymBackgroundWhite, // Clean look
    surfaceVariant = GymInputGray, // For inputs/cards separation if needed
    onSurface = GymDarkText,
    onBackground = GymDarkText,
    onSurfaceVariant = GymDarkText, // Text on inputs
    error = GymError
)

@Composable
fun GymLogTheme(
    // Switch to Light Mode for the new palette
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+ but we disable it to enforce our branding
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> LightColorScheme // Fallback to Light even if dark requested, as per simplified user request
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Icons should be DARK since background is WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true 
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}