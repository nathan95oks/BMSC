package com.bmcs.app.ui.cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmcs.app.R
import com.bmcs.app.ui.cards.api.CardRepository
import kotlinx.coroutines.delay

/**
 * Full-screen "Open Pack" experience.
 *
 * On entry, queries `/sobres/elegibilidad` for the user. The UI then branches:
 *  - Loading      → spinner while we wait for eligibility
 *  - Error        → friendly retry card if the network call failed
 *  - 0 disponibles → "no more sobres this month" empty state, button disabled
 *  - Otherwise    → original 3-D rotating pack with an "Abrir Sobre" button
 */
@Composable
fun PackOpeningScreen(
    onOpenComplete: () -> Unit
) {
    val repository = remember { CardRepository() }

    var availableCount by remember { mutableStateOf<Int?>(null) }
    var maxCount by remember { mutableIntStateOf(0) }
    var usedCount by remember { mutableIntStateOf(0) }
    var eligibilityError by remember { mutableStateOf<String?>(null) }
    var fetchKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(fetchKey) {
        availableCount = null
        eligibilityError = null
        try {
            // HERE ID=1
            val elig = repository.getEligibility(usuarioId = 1)
            availableCount = elig.sobres_disponibles
            maxCount = elig.max_sobres_mes
            usedCount = elig.sobres_usados_mes
        } catch (e: Exception) {
            eligibilityError = e.message ?: "Error de conexión con el servidor"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1a4d2e), Color(0xFF2d5f3f), Color(0xFF1a4d2e))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            availableCount == null && eligibilityError == null ->
                LoadingState()

            eligibilityError != null ->
                EligibilityErrorState(
                    message = eligibilityError!!,
                    onRetry = { fetchKey++ }
                )

            availableCount == 0 ->
                NoSobresState(usedCount = usedCount, maxCount = maxCount)

            else ->
                ReadyToOpenState(
                    availableCount = availableCount!!,
                    onOpenComplete = onOpenComplete
                )
        }
    }
}

// ── States ──────────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            color = Color(0xFFFBBF24),
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Verificando sobres…",
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EligibilityErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        // Warning emoji inside a soft circle
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFFFBBF24).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("⚠", fontSize = 56.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "No pudimos conectarnos",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFBBF24),
                contentColor = Color(0xFF1a4d2e)
            ),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 14.dp),
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color(0xFFFBBF24).copy(alpha = 0.4f),
                spotColor = Color(0xFFFBBF24).copy(alpha = 0.4f)
            )
        ) {
            Text("Reintentar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NoSobresState(usedCount: Int, maxCount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        // Empty mailbox icon
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("📭", fontSize = 68.sp)
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Ya no tienes sobres",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Usaste todos tus sobres de este mes.\nVuelve el próximo mes para obtener más.",
            color = Color.White.copy(alpha = 0.70f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        if (maxCount > 0) {
            Spacer(Modifier.height(26.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(horizontal = 22.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$usedCount / $maxCount sobres usados",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ReadyToOpenState(
    availableCount: Int,
    onOpenComplete: () -> Unit
) {
    var isOpening by remember { mutableStateOf(false) }

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
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
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

    LaunchedEffect(isOpening) {
        if (isOpening) {
            delay(750)
            onOpenComplete()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // ── Header ─────────────────────────────────────────────────────────
        Text(
            text = "Módulo Gamificado",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Normal
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Abrir Sobre",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        // ── Available-count badge ──────────────────────────────────────────
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFFBBF24).copy(alpha = 0.18f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (availableCount == 1) "1 sobre disponible"
                else "$availableCount sobres disponibles",
                color = Color(0xFFFBBF24),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(36.dp))

        // ── 3-D Pack ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .scale(openScale)
                .alpha(openAlpha),
            contentAlignment = Alignment.Center
        ) {
            // Glow behind pack
            Box(
                modifier = Modifier
                    .size(width = 240.dp, height = 340.dp)
                    .graphicsLayer { alpha = glowAlpha }
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
                val showFront = (rotationY % 360f) < 180f || isOpening

                if (showFront) {
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
                                            start = Offset(size.width * shineOffset, 0f),
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
                                            start = Offset(size.width * shineOffset, 0f),
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

        Spacer(Modifier.height(48.dp))

        // ── Open Button ────────────────────────────────────────────────────
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
