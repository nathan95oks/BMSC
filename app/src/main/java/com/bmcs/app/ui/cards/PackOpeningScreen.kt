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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
 * Full-screen "Open Pack" experience — faithful port of the React TSX
 * 3-D rotating pack.
 *
 * Both the front and back faces are rendered at all times; each face carries
 * the **parent** rotation plus its own offset (0° for front, 180° for back).
 * The face that is "looking away" is hidden via alpha = 0 based on the
 * normalised rotation angle, replicating CSS `backface-visibility: hidden`
 * without any jarring conditional swap.
 *
 * When the user taps **Abrir Sobre**, the rotation stops, the pack scales
 * up + fades out (700 ms, matching the React `transition-all duration-700`),
 * and then [onOpenComplete] is invoked so the host can transition to the
 * card-reveal screen.
 */
@Composable
fun PackOpeningScreen(
    onOpenComplete: () -> Unit
) {
    // ── State ──────────────────────────────────────────────────────────────
    var isOpening by remember { mutableStateOf(false) }
    var isRotating by remember { mutableStateOf(true) }

    // ── Continuous Y-rotation (pack flip) ─────────────────────────────────
    // Matches the React `animate-rotate-y` keyframe.
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

    // Freeze rotation when opening
    val displayRotation = if (isRotating) loopAngle else 0f

    // ── Shine sweep (diagonal bar, matches CSS animate-shine) ─────────────
    // Translates from -150% to +150% on both axes, 3.5s,
    // cubic-bezier(0.4, 0, 0.2, 1) = FastOutSlowInEasing.
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )

    // ── Glow pulse (golden halo behind pack) ──────────────────────────────
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // ── Opening exit animation (scale-up 110 % + fade-out, 700 ms) ───────
    // Mirrors the React `scale-110 opacity-0` with `transition-all duration-700`.
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

    // Navigate to the reveal screen once the exit animation has finished
    LaunchedEffect(isOpening) {
        if (isOpening) {
            delay(750L)
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
            // ── Header (matches the React header) ──────────────────────────
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
            // Outer wrapper applies the opening scale + fade.
            Box(
                modifier = Modifier
                    .scale(openScale)
                    .alpha(openAlpha),
                contentAlignment = Alignment.Center
            ) {
                // Glow halo (behind the card) — bleeds to all sides
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

                // ── Card with perspective ──────────────────────────────────
                // Both faces are ALWAYS rendered, stacked on top of each
                // other. The one whose normalised rotation puts it "facing
                // away" is hidden via alpha = 0, replicating CSS
                // `backface-visibility: hidden` with `preserve-3d`.

                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(320.dp)
                ) {
                    // Helper: normalise any angle into 0..360
                    val normFront = ((displayRotation % 360f) + 360f) % 360f
                    val normBack  = (((displayRotation + 180f) % 360f) + 360f) % 360f

                    val frontVisible = normFront < 90f || normFront > 270f
                    val backVisible  = normBack  < 90f || normBack  > 270f

                    // ── FRONT face ─────────────────────────────────────────
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
                        // Shine sweep overlay
                        ShineOverlay(shineProgress)
                    }

                    // ── BACK face ──────────────────────────────────────────
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
                        // Shine sweep overlay
                        ShineOverlay(shineProgress)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Open Button ────────────────────────────────────────────────
            Button(
                onClick = {
                    isOpening = true
                    isRotating = false
                },
                enabled = !isOpening,
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
                    text = if (isOpening) "Abriendo..." else "✨ Abrir Sobre",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Diagonal light-bar sweep that replicates the CSS:
 *
 * ```css
 * .animate-shine {
 *   width: 200%; height: 50px;
 *   background: rgba(255,255,255,0.25);
 *   filter: blur(10px);
 *   transform: rotate(45deg);
 *   animation: shine 3.5s cubic-bezier(0.4,0,0.2,1) infinite;
 * }
 * @keyframes shine {
 *   0%   { translate(-150%, -150%) rotate(45deg) }
 *   100% { translate( 150%,  150%) rotate(45deg) }
 * }
 * ```
 *
 * [progress] goes from -1.5 → 1.5 (matching -150 % → 150 %).
 */
@Composable
private fun ShineOverlay(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),   // keep shine inside card bounds
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                // w-[200%] of 220dp card = 440dp, h-[50px] = 50dp
                .width(440.dp)
                .height(500.dp)
                .graphicsLayer {
                    // translateX/Y from -150 % to +150 % of the card size
                    translationX = size.width * progress
                    translationY = size.height * progress
                    rotationZ = 45f
                }
                .blur(10.dp)
                .background(Color.White.copy(alpha = 0.25f))
        )
    }
}
