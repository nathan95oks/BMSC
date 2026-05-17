package com.bmcs.app.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmcs.app.ui.cards.api.CardRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CardRevealAnimation(
    onPackConsumed: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val repository = remember { CardRepository() }

    // ── Network state ──────────────────────────────────────────────────────────
    var loadedCards by remember { mutableStateOf<List<BMSCardData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Incrementing this key triggers a fresh server fetch (initial load + replay)
    var fetchKey by remember { mutableIntStateOf(0) }

    // ── Card-stack animation state ─────────────────────────────────────────────
    // Defined before the LaunchedEffect that references them.
    var topIndex by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }

    // Animated depth position per card slot (0.0 = top, 1.0 = one behind, …)
    val stackPositions = remember { List(5) { i -> Animatable(i.toFloat()) } }

    // Exit-animation state (only active for the current top card)
    val exitTx = remember { Animatable(0f) }
    val exitRz = remember { Animatable(0f) }
    val exitRy = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }

    // ── Fetch from server whenever fetchKey changes ────────────────────────────
    LaunchedEffect(fetchKey) {
        isLoading = true
        errorMsg = null
        try {
            // HERE ID=1
            val cards = repository.openNextSobre(usuarioId = 1)
            loadedCards = cards
            LastPackState.update(cards)
        } catch (e: Exception) {
            errorMsg = e.message ?: "Error al cargar las cartas"
        }
        isLoading = false
    }

    // Reset animation state each time a new card batch arrives
    LaunchedEffect(loadedCards) {
        topIndex = 0
        isAnimating = false
        exitTx.snapTo(0f); exitRz.snapTo(0f); exitRy.snapTo(0f); exitAlpha.snapTo(1f)
        stackPositions.forEachIndexed { i, anim -> anim.snapTo(i.toFloat()) }
    }

    // ── Tap handler ────────────────────────────────────────────────────────────
    fun onTap() {
        if (isLoading || isAnimating) return

        if (errorMsg != null) {
            // Retry on tap when in error state
            fetchKey++
            return
        }

        if (topIndex >= loadedCards.size) {
            // All cards revealed — go back to pack opening screen or re-fetch
            if (onPackConsumed != null) {
                onPackConsumed()
            } else {
                fetchKey++
            }
            return
        }

        isAnimating = true
        scope.launch {
            // 1. Fly top card off to the right with 3-D rotation
            coroutineScope {
                launch {
                    exitTx.animateTo(
                        targetValue = 1600f,
                        animationSpec = tween(380, easing = FastOutLinearInEasing)
                    )
                }
                launch { exitRz.animateTo(22f, tween(380)) }
                launch { exitRy.animateTo(42f, tween(380)) }
                launch {
                    delay(170)
                    exitAlpha.animateTo(0f, tween(210))
                }
            }

            // 2. Reset exit state BEFORE advancing topIndex so the new top card
            //    never flashes at the exited transform values for one frame
            exitTx.snapTo(0f); exitRz.snapTo(0f); exitRy.snapTo(0f); exitAlpha.snapTo(1f)

            // 3. Advance
            topIndex++

            // 4. Slide remaining cards forward with a bouncy spring
            stackPositions.forEachIndexed { i, anim ->
                launch {
                    anim.animateTo(
                        targetValue = (i - topIndex).toFloat().coerceAtLeast(0f),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
            }

            isAnimating = false
        }
    }

    // ── UI ─────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D1B2A), Color(0xFF1B2838), Color(0xFF0D1B2A))
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onTap() },
        contentAlignment = Alignment.Center
    ) {
        when {
            // ── Loading ─────────────────────────────────────────────────────
            isLoading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFD700),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Abriendo sobre…",
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 16.sp
                    )
                }
            }

            // ── Error ───────────────────────────────────────────────────────
            errorMsg != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("⚠", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Tap to retry",
                        color = Color.White.copy(alpha = 0.50f),
                        fontSize = 14.sp
                    )
                }
            }

            // ── All cards revealed ──────────────────────────────────────────
            topIndex >= loadedCards.size && loadedCards.isNotEmpty() -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("★", color = Color(0xFFFFD700), fontSize = 56.sp)
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "¡Todas las cartas reveladas!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap to open another pack",
                        color = Color.White.copy(alpha = 0.50f),
                        fontSize = 14.sp
                    )
                }
            }

            // ── Card stack ──────────────────────────────────────────────────
            else -> {
                val totalCards = loadedCards.size

                // Render back-to-front so the top card sits visually on top
                for (i in (topIndex until totalCards).reversed()) {
                    BMSCard(
                        cardData = loadedCards[i],
                        modifier = Modifier
                            .fillMaxWidth(0.76f)
                            .graphicsLayer {
                                val pos = stackPositions[i].value
                                scaleX = (1f - pos * 0.045f).coerceAtLeast(0.82f)
                                scaleY = scaleX
                                translationY = pos * 20f * density
                                rotationX = pos * 1.5f
                                cameraDistance = 12f * density

                                if (i == topIndex) {
                                    rotationX = 0f  // top card stands upright while exiting
                                    translationX = exitTx.value
                                    rotationZ = exitRz.value
                                    rotationY = exitRy.value
                                    alpha = exitAlpha.value
                                }
                            }
                    )
                }

                // Card counter
                Text(
                    text = "${topIndex + 1} / $totalCards",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 52.dp),
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                // Tap hint — fades out while animating
                AnimatedVisibility(
                    visible = !isAnimating,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 52.dp)
                ) {
                    Text(
                        "Tap to reveal",
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
