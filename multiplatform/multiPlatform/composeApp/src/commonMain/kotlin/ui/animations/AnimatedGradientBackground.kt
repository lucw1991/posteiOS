package ui.animations

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


// Gradient color flow. If using this, make the logo less transparent. I think 0.6 looks best here.
@Composable
fun AnimatedGradientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")

    val color by infiniteTransition.animateColor(initialValue = Color(0xFF4A90E2),
        targetValue = Color(0xFF50E3C2),
        animationSpec = infiniteRepeatable(
            tween(4000,
                easing = LinearEasing),
            RepeatMode.Reverse),
        label = "color")

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(color,
        Color.Black))))

}