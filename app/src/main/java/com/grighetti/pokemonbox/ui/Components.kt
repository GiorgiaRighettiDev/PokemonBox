package com.grighetti.pokemonbox.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays a Pokémon type badge with a rounded background.
 *
 * @param type The name of the Pokémon type (e.g., "Fire", "Water").
 */
@Composable
fun TypeBadge(type: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .background(Color(0xFFeeeeee))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = type.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(vertical = 0.dp),
            color = Color(0xFF808080),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * A placeholder box with a shimmer effect, typically used as a loading placeholder.
 *
 * @param modifier Modifier to customize the layout.
 */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shimmerEffect()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
    )
}

/**
 * Adds a shimmer animation effect to a composable.
 * This is useful for creating a loading placeholder effect.
 *
 * @return A modified [Modifier] with the shimmer effect applied.
 */
@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "ShimmerEffect")

    val shimmerTranslate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ShimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.Gray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(shimmerTranslate, 0f),
        end = Offset(shimmerTranslate + 200f, 0f)
    )

    return this
        .clip(RoundedCornerShape(4.dp))
        .background(shimmerBrush)
}

/**
 * Displays either a loading indicator or the actual content based on the loading state.
 *
 * @param isLoading Boolean flag indicating whether the content is loading.
 * @param loading Composable to be shown while loading.
 * @param content The actual content to display once loading is complete.
 */
@Composable
fun LoadingContent(
    isLoading: Boolean,
    loading: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        loading()
    } else {
        content()
    }
}
