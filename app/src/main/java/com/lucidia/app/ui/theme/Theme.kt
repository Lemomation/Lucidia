package com.lucidia.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LucidiaColorScheme = darkColorScheme(
    primary = CoralCardBackground,
    onPrimary = PureWhite,
    primaryContainer = CoralCardDark,
    onPrimaryContainer = PureWhite,
    secondary = TealContainer,
    onSecondary = PureWhite,
    background = NightSkyTop,
    onBackground = PureWhite,
    surface = TealContainer,
    onSurface = PureWhite,
    surfaceVariant = TealSurfaceVariant,
    onSurfaceVariant = PureWhite
)

@Composable
fun LucidiaTheme(
    twilightFilterEnabled: Boolean = false,
    twilightIntensity: Int = 70,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LucidiaColorScheme,
        typography = LucidiaTypography
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            // Twilight Circadian Melatonin Shield Overlay
            if (twilightFilterEnabled) {
                val alpha = (twilightIntensity.coerceIn(10, 95) / 100f) * 0.35f
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFF3D00).copy(alpha = alpha))
                )
            }
        }
    }
}
