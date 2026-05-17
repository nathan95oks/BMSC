package com.bmcs.app.ui.cards

import androidx.compose.ui.graphics.Color
import com.bmcs.app.ui.album.CardRarity
import com.bmcs.app.ui.album.CollectibleCard

data class BMSCardData(
    val id: String = "",
    val title: String,
    val description: String,
    val rarity: BMSRarity,
    val imageUrl: String? = null,
    val points: Int = 0
)

enum class BMSRarity(
    val serverKey: String,       // matches "rareza" / "rareza_obtenida" from API
    val displayLabel: String,
    val cardColor: Color,
    val cardColorLight: Color,
    val borderColor: Color,
    val points: Int
) {
    COMUN(
        serverKey = "comun",
        displayLabel = "Comun",
        cardColor = Color(0xFF7EA850),
        cardColorLight = Color(0xFFBDD785),
        borderColor = Color(0xFFFFD700),
        points = 5
    ),
    RARA(
        serverKey = "rara",
        displayLabel = "Rara",
        cardColor = Color(0xFF1565C0),
        cardColorLight = Color(0xFF64B5F6),
        borderColor = Color(0xFF42A5F5),
        points = 7
    ),
    EPICA(
        serverKey = "epica",
        displayLabel = "Épica",
        cardColor = Color(0xFF6A3FA0),
        cardColorLight = Color(0xFFAA82D4),
        borderColor = Color(0xFFB8960C),
        points = 8
    ),
    LEGENDARIA(
        serverKey = "legendaria",
        displayLabel = "Legendaria",
        cardColor = Color(0xFFCC6B00),
        cardColorLight = Color(0xFFFFCC44),
        borderColor = Color(0xFFFFD700),
        points = 10
    ),
    ULTRA_RARA(
        serverKey = "ultra_rara",
        displayLabel = "UltraRara",
        cardColor = Color(0xFFB71C1C),
        cardColorLight = Color(0xFFEF9A9A),
        borderColor = Color(0xFFFF5252),
        points = 15
    ),
    SUPREMA(
        serverKey = "suprema",
        displayLabel = "Suprema",
        cardColor = Color(0xFF1A0A3C),
        cardColorLight = Color(0xFFCE93D8),
        borderColor = Color(0xFFE0E0E0),
        points = 25
    );

    companion object {
        fun fromServerKey(key: String): BMSRarity =
            entries.firstOrNull { it.serverKey == key } ?: COMUN
    }
}

fun CollectibleCard.toBMSCardData(): BMSCardData {
    val bmsRarity = when (rarity) {
        CardRarity.COMMON -> BMSRarity.COMUN
        CardRarity.EPIC -> BMSRarity.EPICA
        CardRarity.LEGENDARY -> BMSRarity.LEGENDARIA
    }
    val pts = when (bmsRarity) {
        BMSRarity.COMUN       -> 0
        BMSRarity.RARA        -> 5
        BMSRarity.EPICA       -> 25
        BMSRarity.LEGENDARIA  -> 100
        BMSRarity.ULTRA_RARA  -> 500
        BMSRarity.SUPREMA     -> 2000
    }
    return BMSCardData(
        id = id.toString(),
        title = name,
        description = description,
        rarity = bmsRarity,
        imageUrl = imageUrl.takeIf { it.isNotBlank() },
        points = pts
    )
}
