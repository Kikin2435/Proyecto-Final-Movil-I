package com.example.proyectomovil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// =================================================================================
// Escala de Tipografía MEDIA (Tus valores actuales o los por defecto)
// =================================================================================
// Esta es tu base. Los otros tamaños se calcularán a partir de esta.
val AppTypographyMedium = Typography(
    // Estilos de Título (Display, Headline, Title)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),

    // Estilos de Cuerpo (Body)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),

    // Estilos de Etiqueta (Label)
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
)

// =================================================================================
// Escala de Tipografía PEQUEÑA (-2sp a cada estilo)
// =================================================================================
val AppTypographySmall = Typography(
    displayLarge = AppTypographyMedium.displayLarge.copy(fontSize = (AppTypographyMedium.displayLarge.fontSize.value - 2).sp),
    headlineLarge = AppTypographyMedium.headlineLarge.copy(fontSize = (AppTypographyMedium.headlineLarge.fontSize.value - 2).sp),
    titleLarge = AppTypographyMedium.titleLarge.copy(fontSize = (AppTypographyMedium.titleLarge.fontSize.value - 2).sp),
    titleMedium = AppTypographyMedium.titleMedium.copy(fontSize = (AppTypographyMedium.titleMedium.fontSize.value - 2).sp),
    bodyLarge = AppTypographyMedium.bodyLarge.copy(fontSize = (AppTypographyMedium.bodyLarge.fontSize.value - 2).sp),
    bodyMedium = AppTypographyMedium.bodyMedium.copy(fontSize = (AppTypographyMedium.bodyMedium.fontSize.value - 2).sp),
    bodySmall = AppTypographyMedium.bodySmall.copy(fontSize = (AppTypographyMedium.bodySmall.fontSize.value - 2).sp),
    labelLarge = AppTypographyMedium.labelLarge.copy(fontSize = (AppTypographyMedium.labelLarge.fontSize.value - 2).sp),
    labelMedium = AppTypographyMedium.labelMedium.copy(fontSize = (AppTypographyMedium.labelMedium.fontSize.value - 2).sp),
    labelSmall = AppTypographyMedium.labelSmall.copy(fontSize = (AppTypographyMedium.labelSmall.fontSize.value - 2).sp),
)

// =================================================================================
// Escala de Tipografía GRANDE (+4sp a cada estilo)
// =================================================================================
val AppTypographyLarge = Typography(
    displayLarge = AppTypographyMedium.displayLarge.copy(fontSize = (AppTypographyMedium.displayLarge.fontSize.value + 4).sp),
    headlineLarge = AppTypographyMedium.headlineLarge.copy(fontSize = (AppTypographyMedium.headlineLarge.fontSize.value + 4).sp),
    titleLarge = AppTypographyMedium.titleLarge.copy(fontSize = (AppTypographyMedium.titleLarge.fontSize.value + 4).sp),
    titleMedium = AppTypographyMedium.titleMedium.copy(fontSize = (AppTypographyMedium.titleMedium.fontSize.value + 4).sp),
    bodyLarge = AppTypographyMedium.bodyLarge.copy(fontSize = (AppTypographyMedium.bodyLarge.fontSize.value + 4).sp),
    bodyMedium = AppTypographyMedium.bodyMedium.copy(fontSize = (AppTypographyMedium.bodyMedium.fontSize.value + 4).sp),
    bodySmall = AppTypographyMedium.bodySmall.copy(fontSize = (AppTypographyMedium.bodySmall.fontSize.value + 4).sp),
    labelLarge = AppTypographyMedium.labelLarge.copy(fontSize = (AppTypographyMedium.labelLarge.fontSize.value + 4).sp),
    labelMedium = AppTypographyMedium.labelMedium.copy(fontSize = (AppTypographyMedium.labelMedium.fontSize.value + 4).sp),
    labelSmall = AppTypographyMedium.labelSmall.copy(fontSize = (AppTypographyMedium.labelSmall.fontSize.value + 4).sp),
)
