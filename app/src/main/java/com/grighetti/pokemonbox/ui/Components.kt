package com.grighetti.pokemonbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
