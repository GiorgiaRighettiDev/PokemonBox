package com.grighetti.pokemonbox.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.grighetti.pokemonbox.R

/**
 * Defines the font family for Bricolage Grotesque, used in titles and headings.
 * Includes multiple weights for different text emphasis.
 */
val BricolageGrotesqueFontFamily = FontFamily(
    Font(R.font.bricolagegrotesque_regular, FontWeight.Normal),
    Font(R.font.bricolagegrotesque_medium, FontWeight.Medium),
    Font(R.font.bricolagegrotesque_bold, FontWeight.Bold),
    Font(R.font.bricolagegrotesque_black, FontWeight.Black)
)

/**
 * Defines the font family for Inter, used primarily for body text.
 * Provides a clean and readable font style for descriptions and general content.
 */
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_black, FontWeight.Black)
)
