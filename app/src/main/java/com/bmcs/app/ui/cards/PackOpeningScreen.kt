package com.bmcs.app.ui.cards

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bmcs.app.R
import kotlinx.coroutines.delay

@Composable
fun PackOpeningScreen(
    onOpenComplete: () -> Unit
) {
    // ── Phase state ────────────────────────────────────────────────────────
    var phase by remember { mutableStateOf("idle") }
    var isRotating by remember { mutableStateOf(true) }

    // ── Continuous Y-rotation ─────────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "packLoop")
    val loopAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )
    val displayRotation = if (isRotating) loopAngle else 0f

    // ── Shine sweep ───────────────────────────────────────────────────────
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )

    // ── Glow pulse ────────────────────────────────────────────────────────
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // ── Pack exit animation ───────────────────────────────────────────────
    val isOpening = phase != "idle"
    val openScale by animateFloatAsState(
        targetValue = if (isOpening) 1.10f else 1f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "openScale"
    )
    val openAlpha by animateFloatAsState(
        targetValue = if (isOpening) 0f else 1f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "openAlpha"
    )

    // ── White flash animation ─────────────────────────────────────────────
    val flashAlpha by animateFloatAsState(
        targetValue = if (phase == "flash") 1f else 0f,
        animationSpec = if (phase == "flash")
            tween(durationMillis = 150, easing = LinearEasing)
        else
            tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flash"
    )

    // ── Phase transitions ─────────────────────────────────────────────────
    LaunchedEffect(phase) {
        when (phase) {
            "opening" -> {
                delay(700L)
                phase = "flash"
            }
            "flash" -> {
                delay(300L)
                phase = "video"
            }
        }
    }

    // ── UI ─────────────────────────────────────────────────────────────────
    val showWhiteBg = phase == "flash" || phase == "video"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (showWhiteBg) SolidColor(Color.White)
                else Brush.verticalGradient(
                    listOf(Color(0xFF1a4d2e), Color(0xFF2d5f3f), Color(0xFF1a4d2e))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Layer 1: Pack + header + button (visible during idle) ───────
        if (phase == "idle" || phase == "opening") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
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

                // 3-D Pack Container
                Box(
                    modifier = Modifier
                        .scale(openScale)
                        .alpha(openAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow halo
                    Box(
                        modifier = Modifier
                            .size(width = 460.dp, height = 440.dp)
                            .graphicsLayer { alpha = glowAlpha }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFD4AF37).copy(alpha = 0.55f),
                                        Color(0xFFD4AF37).copy(alpha = 0.25f),
                                        Color.Transparent
                                    ),
                                    radius = 500f
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )

                    // Card with perspective
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                            .height(320.dp)
                    ) {
                        val normFront = ((displayRotation % 360f) + 360f) % 360f
                        val normBack  = (((displayRotation + 180f) % 360f) + 360f) % 360f
                        val frontVisible = normFront < 90f || normFront > 270f
                        val backVisible  = normBack  < 90f || normBack  > 270f

                        // FRONT face
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    rotationY = displayRotation
                                    cameraDistance = 12f * density
                                    alpha = if (frontVisible) 1f else 0f
                                }
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pack_front),
                                contentDescription = "Sobre MSC Frente",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            ShineOverlay(shineProgress)
                        }

                        // BACK face
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    rotationY = displayRotation + 180f
                                    cameraDistance = 12f * density
                                    alpha = if (backVisible) 1f else 0f
                                }
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pack_back),
                                contentDescription = "Sobre MSC Reverso",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            ShineOverlay(shineProgress)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Open Button
                Button(
                    onClick = {
                        isRotating = false
                        phase = "opening"
                    },
                    enabled = phase == "idle",
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBBF24),
                        contentColor = Color(0xFF1a4d2e),
                        disabledContainerColor = Color(0xFFFBBF24).copy(alpha = 0.5f),
                        disabledContentColor = Color(0xFF1a4d2e).copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
                    modifier = Modifier.alpha(openAlpha)
                ) {
                    Text(
                        text = if (phase != "idle") "Abriendo..." else "✨ Abrir Sobre",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ── Layers 2 & 3: Video Full-Width & Auto-Height ──────────────────
        if (phase == "video") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            // Obliga al componente nativo a tomar todo el ancho pero envolver su propio contenido en el alto (mantiene proporción)
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )

                            setMediaController(null)
                            setOnTouchListener { _, _ -> true }
                            isFocusable = false
                            isFocusableInTouchMode = false

                            val videoUri = Uri.parse(
                                "android.resource://${ctx.packageName}/${R.raw.animacion}"
                            )
                            setVideoURI(videoUri)
                            setOnPreparedListener { mp ->
                                mp.isLooping = false
                                mp.playbackParams = mp.playbackParams.setSpeed(2.0f)
                                start()
                            }
                            setOnCompletionListener {
                                onOpenComplete()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth() // En Compose le indicamos que se expanda horizontalmente
                )
            }
        }

        // ── Layer 4: White flash overlay (on top of everything) ────────
        if (flashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(flashAlpha)
                    .background(Color.White)
            )
        }
    }
}

@Composable
private fun ShineOverlay(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(440.dp)
                .height(50.dp)
                .graphicsLayer {
                    translationX = size.width * progress
                    translationY = size.height * progress
                    rotationZ = 45f
                }
                .blur(10.dp)
                .background(Color.White.copy(alpha = 0.25f))
        )
    }
}