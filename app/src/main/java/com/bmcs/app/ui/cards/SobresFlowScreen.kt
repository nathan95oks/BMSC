package com.bmcs.app.ui.cards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Two-step "Sobres" experience:
 *  1. [PackOpeningScreen] — 3-D rotating pack with "Abrir Sobre" button.
 *  2. [CardRevealAnimation] — card stack reveal after opening the pack.
 *
 * When all cards have been revealed the user can tap again to return to step 1.
 */
@Composable
fun SobresFlowScreen() {
    var showReveal by rememberSaveable { mutableStateOf(false) }

    AnimatedContent(
        targetState = showReveal,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "sobresFlow"
    ) { revealCards ->
        if (revealCards) {
            CardRevealAnimation(
                onPackConsumed = {
                    // Called when the user wants to open another pack
                    showReveal = false
                }
            )
        } else {
            PackOpeningScreen(
                onOpenComplete = {
                    showReveal = true
                }
            )
        }
    }
}
