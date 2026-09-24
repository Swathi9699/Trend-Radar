package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlameGold
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.NeonCoral

@Composable
fun ViralityMeter(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    strokeWidth: Dp = 5.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "viralityProgress"
    )

    val gradientBrush = when {
        score >= 90 -> Brush.sweepGradient(listOf(NeonCoral, FlameGold, NeonCoral))
        score >= 80 -> Brush.sweepGradient(listOf(FlameGold, HyperCyan, FlameGold))
        else -> Brush.sweepGradient(listOf(HyperCyan, Color(0xFFA259FF), HyperCyan))
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            // Background track
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                style = Stroke(width = strokePx)
            )
            // Progress arc
            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = (size.value * 0.32f).sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "INDEX",
                fontSize = (size.value * 0.13f).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9EABCA)
            )
        }
    }
}
