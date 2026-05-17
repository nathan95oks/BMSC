package com.bmcs.app.ui.cards

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global singleton that caches the cards from the most recently opened sobre.
 * The Album tab observes [lastCards] to mark newly obtained cards as owned.
 */
object LastPackState {
    private val _lastCards = MutableStateFlow<List<BMSCardData>>(emptyList())
    val lastCards: StateFlow<List<BMSCardData>> = _lastCards.asStateFlow()

    private val _totalPoints = MutableStateFlow(2450)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    fun update(cards: List<BMSCardData>) {
        _lastCards.value = cards
        _totalPoints.value += cards.sumOf { it.rarity.points }
    }
}
