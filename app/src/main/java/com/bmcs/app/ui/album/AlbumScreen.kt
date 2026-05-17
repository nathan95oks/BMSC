package com.bmcs.app.ui.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bmcs.app.ui.cards.BMSCard
import com.bmcs.app.ui.cards.toBMSCardData
import com.bmcs.app.ui.theme.MercantilGreen

// ─── Colores internos del álbum ───────────────────────────────────────────────

private val GoldStart    = Color(0xFFFFD700)
private val GoldEnd      = Color(0xFFFFF9E6)
private val SilverStart  = Color(0xFFBDC3C7)
private val SilverEnd    = Color(0xFFFDFDFD)
private val SurfaceCard  = Color(0xFFF0EDED)
private val OnSurface    = Color(0xFF1B1C1C)
private val OutlineVar   = Color(0xFFBDCABB)
private val Amber        = Color(0xFFFB8C00)
private val GreenDark    = Color(0xFF005324)
private val GreenMid     = Color(0xFF166735)

// ─── Pantalla principal ───────────────────────────────────────────────────────

@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF9F8))
            .verticalScroll(rememberScrollState())
    ) {
        AlbumHeader()
        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Título y progreso
            AlbumProgressSection(state)
            Spacer(Modifier.height(16.dp))

            // Filtros de rareza
            RarityFilterRow(
                selected  = state.rarityFilter,
                onSelect  = viewModel::setRarityFilter
            )
            Spacer(Modifier.height(12.dp))

            // Filtros de posesión
            OwnershipFilterRow(
                selected = state.ownershipFilter,
                onSelect = viewModel::setOwnershipFilter
            )
            Spacer(Modifier.height(12.dp))

            // Paginación
            if (state.totalPages > 1) {
                PageSelector(
                    currentPage = state.currentPage,
                    totalPages  = state.totalPages,
                    onPageSelected = viewModel::setPage
                )
                Spacer(Modifier.height(16.dp))
            }

            // Cuadrícula del álbum
            AlbumGrid(cards = state.pagedCards)
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun AlbumHeader() {
    Surface(
        color     = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text       = "BMSC Álbum",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MercantilGreen
                )
                Text(
                    text     = "Historia y Cultura de Bolivia",
                    fontSize = 12.sp,
                    color    = Color(0xFF6E7A6E)
                )
            }

            // Chip de puntos
            Surface(
                color  = MercantilGreen,
                shape  = CircleShape,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Star,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(16.dp)
                    )
                    Text(
                        text       = "250 pts",
                        color      = Color.White,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Sección de progreso ──────────────────────────────────────────────────────

@Composable
private fun AlbumProgressSection(state: AlbumUiState) {
    val animatedProgress by animateFloatAsState(
        targetValue    = state.progressPercent,
        animationSpec  = tween(800),
        label          = "progress"
    )

    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text     = "Progreso: ${state.ownedCards} / ${state.totalCards} figuritas",
                fontSize = 12.sp,
                color    = Color(0xFF6E7A6E),
                fontWeight = FontWeight.Medium
            )
            Text(
                text       = "${(state.progressPercent * 100).toInt()}% completado",
                fontSize   = 12.sp,
                color      = MercantilGreen,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress          = { animatedProgress },
            modifier          = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50)),
            color             = MercantilGreen,
            trackColor        = SurfaceCard,
            strokeCap         = StrokeCap.Round
        )
    }
}

// ─── Filtro de rareza ─────────────────────────────────────────────────────────

@Composable
private fun RarityFilterRow(
    selected: RarityFilter,
    onSelect: (RarityFilter) -> Unit
) {
    Column {
        Text(
            text       = "FILTRAR POR RAREZA",
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF6E7A6E),
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier            = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RarityFilter.entries.forEach { filter ->
                FilterChipItem(
                    label      = filter.label,
                    isSelected = selected == filter,
                    onClick    = { onSelect(filter) }
                )
            }
        }
    }
}

// ─── Filtro de posesión ───────────────────────────────────────────────────────

@Composable
private fun OwnershipFilterRow(
    selected: OwnershipFilter,
    onSelect: (OwnershipFilter) -> Unit
) {
    Column {
        Text(
            text       = "MOSTRAR",
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF6E7A6E),
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OwnershipFilter.entries.forEach { filter ->
                FilterChipItem(
                    label      = filter.label,
                    isSelected = selected == filter,
                    onClick    = { onSelect(filter) }
                )
            }
        }
    }
}

// ─── Chip de filtro reutilizable (mismo estilo que el HTML de referencia) ──────

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick       = onClick,
        color         = if (isSelected) MercantilGreen else Color.White,
        shape         = RoundedCornerShape(8.dp),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        border        = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            text       = label,
            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize   = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color      = if (isSelected) Color.White else Color(0xFF6E7A6E)
        )
    }
}

// ─── Selector de páginas ──────────────────────────────────────────────────────

@Composable
private fun PageSelector(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit
) {
    Column {
        Text(
            text       = "PÁGINAS",
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = Color(0xFF6E7A6E),
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier              = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (1..totalPages).forEach { page ->
                val isSelected = page == currentPage
                Surface(
                    onClick       = { onPageSelected(page) },
                    shape         = RoundedCornerShape(8.dp),
                    color         = if (isSelected) MercantilGreen else Color.White,
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    border        = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null,
                    modifier      = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text       = "$page",
                            fontSize   = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color      = if (isSelected) Color.White else OnSurface
                        )
                    }
                }
            }
        }
    }
}

// ─── Cuadrícula del álbum ─────────────────────────────────────────────────────

@Composable
private fun AlbumGrid(cards: List<CollectibleCard>) {
    // Fondo tipo cuaderno del álbum
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, OutlineVar, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Línea central del álbum (efecto libro)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(2.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, OutlineVar.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )

        if (cards.isEmpty()) {
            EmptyAlbumMessage()
        } else {
            // Grid 2 columnas manual (para evitar LazyVerticalGrid dentro de scroll vertical)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                cards.chunked(2).forEach { rowCards ->
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCards.forEach { card ->
                            AlbumCardItem(
                                card     = card,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Relleno si la fila tiene un solo elemento
                        if (rowCards.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// ─── Carta individual ─────────────────────────────────────────────────────────

@Composable
private fun AlbumCardItem(
    card: CollectibleCard,
    modifier: Modifier = Modifier
) {
    if (!card.isOwned) {
        MissingCardSlot(number = card.number, modifier = modifier)
        return
    }

    Box(modifier = modifier) {
        BMSCard(
            cardData = card.toBMSCardData(),
            modifier = Modifier.fillMaxWidth()
        )

        // "Nueva!" badge overlay
        AnimatedVisibility(
            visible  = card.isNew,
            enter    = fadeIn(),
            exit     = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
        ) {
            Surface(
                color = Amber,
                shape = CircleShape
            ) {
                Text(
                    text       = "¡Nueva!",
                    fontSize   = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    modifier   = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ─── Slot de carta faltante ───────────────────────────────────────────────────

@Composable
private fun MissingCardSlot(
    number: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier          = modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, OutlineVar.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .background(SurfaceCard.copy(alpha = 0.5f)),
        contentAlignment  = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.HelpOutline,
                contentDescription = "Carta faltante",
                tint               = OutlineVar,
                modifier           = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "No. $number",
                fontSize   = 9.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF6E7A6E)
            )
        }
    }
}

// ─── Estado vacío ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyAlbumMessage() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector        = Icons.Filled.HelpOutline,
            contentDescription = null,
            tint               = OutlineVar,
            modifier           = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text       = "No hay cartas con estos filtros",
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            color      = Color(0xFF6E7A6E),
            textAlign  = TextAlign.Center
        )
    }
}
