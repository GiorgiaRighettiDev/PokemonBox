package com.grighetti.pokemonbox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Personalizza la tipografia dell'app con Montserrat
val Typography = Typography(

    headlineMedium = TextStyle(
        fontFamily = BricolageGrotesqueFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.8).sp,
        color = Color(0xFF202749)
    ),
    titleMedium = TextStyle(
        fontFamily = BricolageGrotesqueFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.6).sp,
        color = Color(0xff290402)
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF808080)
    ),


    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
