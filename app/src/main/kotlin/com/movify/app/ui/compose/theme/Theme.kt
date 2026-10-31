package com.movify.app.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MovifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) MovifyDarkColorScheme else MovifyLightColorScheme,
        typography = MovifyTypography,
        shapes = MovifyShapes,
        content = content,
    )
}
