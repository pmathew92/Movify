package com.movify.app.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun movifyTextStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: Double = 0.0,
) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
)

val MovifyTypography = Typography(
    displayLarge = movifyTextStyle(57, 64, letterSpacing = -0.25),
    displayMedium = movifyTextStyle(45, 52),
    displaySmall = movifyTextStyle(36, 44),
    headlineLarge = movifyTextStyle(32, 40),
    headlineMedium = movifyTextStyle(28, 36),
    headlineSmall = movifyTextStyle(24, 32),
    // Match the existing movie title's 20sp bold treatment.
    titleLarge = movifyTextStyle(20, 28, FontWeight.Bold),
    titleMedium = movifyTextStyle(16, 24, FontWeight.Medium, letterSpacing = 0.15),
    titleSmall = movifyTextStyle(14, 20, FontWeight.Medium, letterSpacing = 0.1),
    bodyLarge = movifyTextStyle(16, 24, letterSpacing = 0.5),
    bodyMedium = movifyTextStyle(14, 20, letterSpacing = 0.25),
    bodySmall = movifyTextStyle(12, 16, letterSpacing = 0.4),
    labelLarge = movifyTextStyle(14, 20, FontWeight.Medium, letterSpacing = 0.1),
    labelMedium = movifyTextStyle(12, 16, FontWeight.Medium, letterSpacing = 0.5),
    labelSmall = movifyTextStyle(11, 16, FontWeight.Medium, letterSpacing = 0.5),
)
