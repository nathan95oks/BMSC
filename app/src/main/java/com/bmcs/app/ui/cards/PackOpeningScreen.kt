package com.bmcs.app.ui.cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmcs.app.R
import kotlinx.coroutines.delay

/**
 * Full-screen "Open Pack" experience.
 *
 * Shows a continuously-rotating 3-D pack card (front / back) with a golden
 * glow and a sweep-shine effect. When the user taps the **Abrir Sobre**
 * button the pack scales up and fades out, then [onOpenComplete] is called so
 * the host can navigate to the card-reveal screen.
 */
@Composable
fun PackOpeningScreen(
    onOpenComplete: () -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────────
    var isOpening by remember { mutableStateOf(false) }

    // ── Continuous Y-rotation (pack flip) ─────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "packRotation")
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )

    // ── Shine sweep ───────────────────────────────────────────────────────
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )

    // ── Glow pulse ────────────────────────────────────────────────────────
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // ── Opening animation (scale-up + fade-out) ───────────────────────────
    val openScale by animateFloatAsState(
        targetValue = if (isOpening) 1.25f else 1f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "openScale"
    )
    val openAlpha by animateFloatAsState(
        targetValue = if (isOpening) 0f else 1f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "openAlpha"
    )

    // Navigate to reveal screen after the exit animation completes
    LaunchedEffect(isOpening) {
        if (isOpening) {
            delay(750)
            onOpenComplete()
        }
    }

    // ── UI ─────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1a4d2e),
                        Color(0xFF2d5f3f),
                        Color(0xFF1a4d2e)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Header ─────────────────────────────────────────────────────
            Text(
                text = "Módulo Gamificado",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Abrir Sobre",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── 3-D Pack Container ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .scale(openScale)
                    .alpha(openAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect behind pack
                Box(
                    modifier = Modifier
                        .size(width = 240.dp, height = 340.dp)
                        .graphicsLayer {
                            alpha = glowAlpha
                        }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFD4AF37).copy(alpha = 0.6f),
                                    Color.Transparent
                                ),
                                radius = 400f
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                // Pack card with 3-D rotation
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(320.dp)
                        .graphicsLayer {
                            this.rotationY = if (isOpening) 0f else rotationY
                            cameraDistance = 12f * density
                            transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
                        }
                ) {
                    // Determine which face to show based on rotation angle
                    val showFront = (rotationY % 360f) < 180f || isOpening

                    if (showFront) {
                        // Front of Pack
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .shadow(
                                    elevation = 24.dp,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pack_front),
                                contentDescription = "Sobre MSC Frente",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Shine sweep overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.White.copy(alpha = 0.22f),
                                                    Color.Transparent
                                                ),
                                                start = Offset(
                                                    size.width * shineOffset,
                                                    0f
                                                ),
                                                end = Offset(
                                                    size.width * (shineOffset + 0.5f),
                                                    size.height
                                                )
                                            )
                                        )
                                    }
                            )
                        }
                    } else {
                        // Back of Pack (mirrored so it reads correctly)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .shadow(
                                    elevation = 24.dp,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .graphicsLayer { scaleX = -1f }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pack_back),
                                contentDescription = "Sobre MSC Reverso",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Shine sweep overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.White.copy(alpha = 0.22f),
                                                    Color.Transparent
                                                ),
                                                start = Offset(
                                                    size.width * shineOffset,
                                                    0f
                                                ),
                                                end = Offset(
                                                    size.width * (shineOffset + 0.5f),
                                                    size.height
                                                )
                                            )
                                        )
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Open Button ────────────────────────────────────────────────
            Button(
                onClick = { isOpening = true },
                enabled = !isOpening,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBBF24),
                    contentColor = Color(0xFF1a4d2e),
                    disabledContainerColor = Color(0xFFFBBF24).copy(alpha = 0.5f),
                    disabledContentColor = Color(0xFF1a4d2e).copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
                modifier = Modifier
                    .alpha(openAlpha)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = Color(0xFFFBBF24).copy(alpha = 0.5f),
                        spotColor = Color(0xFFFBBF24).copy(alpha = 0.5f)
                    )
            ) {
                Text(
                    text = if (isOpening) "Abriendo..." else "✨ Abrir Sobre",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
