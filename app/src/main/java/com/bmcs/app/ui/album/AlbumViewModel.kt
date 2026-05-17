package com.bmcs.app.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmcs.app.ui.cards.LastPackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── Estados de filtro ────────────────────────────────────────────────────────

enum class RarityFilter(val label: String) {
    ALL("Todas"),
    COMMON("Comunes"),
    EPIC("Épicas"),
    LEGENDARY("Legendarias")
}

enum class OwnershipFilter(val label: String) {
    ALL("Todas"),
    OWNED("Obtenidas"),
    MISSING("Faltantes")
}

// ─── UI State ────────────────────────────────────────────────────────────────

data class AlbumUiState(
    val allCards: List<CollectibleCard>        = sampleCards,
    val filteredCards: List<CollectibleCard>   = sampleCards,
    val rarityFilter: RarityFilter             = RarityFilter.ALL,
    val ownershipFilter: OwnershipFilter       = OwnershipFilter.ALL,
    val currentPage: Int                       = 1,
    val totalPages: Int                        = 1,
    val searchQuery: String                    = "",
    val isLoading: Boolean                     = false
) {
    val totalCards: Int   get() = allCards.size
    val ownedCards: Int   get() = allCards.count { it.isOwned }
    val progressPercent: Float get() = if (totalCards == 0) 0f else ownedCards.toFloat() / totalCards

    // Cartas de la página actual (6 por página — 3 filas × 2 columnas)
    val pagedCards: List<CollectibleCard> get() {
        val pageSize = PAGE_SIZE
        val from = (currentPage - 1) * pageSize
        val to   = minOf(from + pageSize, filteredCards.size)
        return if (from >= filteredCards.size) emptyList() else filteredCards.subList(from, to)
    }

    companion object {
        const val PAGE_SIZE = 6
    }
}

// ─── ViewModel ───────────────────────────────────────────────────────────────

class AlbumViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumUiState())
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        // Observe the last opened pack and mark matching cards as owned
        viewModelScope.launch {
            LastPackState.lastCards
                .filterNot { it.isEmpty() }
                .collect { newCards ->
                    val newIds = newCards.mapNotNull { it.id.toIntOrNull() }.toSet()
                    _uiState.update { state ->
                        val updated = state.allCards.map { card ->
                            if (card.id in newIds && !card.isOwned)
                                card.copy(isOwned = true, isNew = true)
                            else
                                card
                        }
                        state.copy(allCards = updated)
                    }
                    applyFilters()
                }
        }
    }

    // Cambia el filtro de rareza
    fun setRarityFilter(filter: RarityFilter) {
        _uiState.update { it.copy(rarityFilter = filter, currentPage = 1) }
        applyFilters()
    }

    // Cambia el filtro de posesión
    fun setOwnershipFilter(filter: OwnershipFilter) {
        _uiState.update { it.copy(ownershipFilter = filter, currentPage = 1) }
        applyFilters()
    }

    // Cambia la página actual
    fun setPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    // Aplica ambos filtros sobre la lista completa
    private fun applyFilters() {
        _uiState.update { state ->
            val filtered = state.allCards
                .filter { card ->
                    when (state.rarityFilter) {
                        RarityFilter.ALL        -> true
                        RarityFilter.COMMON     -> card.rarity == CardRarity.COMMON
                        RarityFilter.EPIC       -> card.rarity == CardRarity.EPIC
                        RarityFilter.LEGENDARY  -> card.rarity == CardRarity.LEGENDARY
                    }
                }
                .filter { card ->
                    when (state.ownershipFilter) {
                        OwnershipFilter.ALL     -> true
                        OwnershipFilter.OWNED   -> card.isOwned
                        OwnershipFilter.MISSING -> !card.isOwned
                    }
                }

            val totalPages = maxOf(1, (filtered.size + AlbumUiState.PAGE_SIZE - 1) / AlbumUiState.PAGE_SIZE)

            state.copy(
                filteredCards = filtered,
                totalPages    = totalPages,
                currentPage   = 1
            )
        }
    }
}
