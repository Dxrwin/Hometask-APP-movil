package com.iub.hometask.features.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onFinished: () -> Unit
) {
    // Timeout de unos segundos
    LaunchedEffect(Unit) {
        delay(2500) // ~2.5s
        onFinished()
    }

    val infinite = rememberInfiniteTransition(label = "loading_transition")

    val scale1 by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale1"
    )

    val scale2 by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale2"
    )

    val alphaPulse by infinite.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            // --- “Tarjetas” animadas tipo skeleton ---
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(80.dp)
                        .scale(scale1)
                        .alpha(alphaPulse)
                        .background(
                            color = TextSecondary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .scale(scale2)
                            .alpha(alphaPulse)
                            .background(
                                color = TextSecondary.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .scale(scale1)
                            .alpha(alphaPulse)
                            .background(
                                color = TextSecondary.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(30.dp)
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                        .scale(scale1)
                        .alpha(alphaPulse)
                        .background(
                            color = TextSecondary.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(24.dp)
                        )
                )
            }

            // --- Icono circular central (placeholder) ---
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale2)
                    .background(
                        color = TextSecondary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // TODO: aquí va el icono animado del logo (ej. Icon(Icons.Outlined.Home))
            }

            // --- Textos ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Preparando tu hogar...",
                    color = TextPrimary,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Estamos organizando todo para ti.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
