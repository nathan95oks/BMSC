package com.bmcs.app.ui.cards

import androidx.compose.ui.graphics.Color
import com.bmcs.app.ui.album.CardRarity
import com.bmcs.app.ui.album.CollectibleCard

data class BMSCardData(
    val id: String = "",
    val title: String,
    val description: String,
    val rarity: BMSRarity,
    val imageUrl: String? = null
)

enum class BMSRarity(
    val serverKey: String,       // matches "rareza" / "rareza_obtenida" from API
    val displayLabel: String,
    val cardColor: Color,
    val cardColorLight: Color,
    val borderColor: Color
) {
    COMUN(
        serverKey = "comun",
        displayLabel = "Basic BMSCard",
        cardColor = Color(0xFF7EA850),
        cardColorLight = Color(0xFFBDD785),
        borderColor = Color(0xFFFFD700)
    ),
    RARA(
        serverKey = "rara",
        displayLabel = "Rara BMSCard",
        cardColor = Color(0xFF1565C0),
        cardColorLight = Color(0xFF64B5F6),
        borderColor = Color(0xFF42A5F5)
    ),
    EPICA(
        serverKey = "epica",
        displayLabel = "Épica BMSCard",
        cardColor = Color(0xFF6A3FA0),
        cardColorLight = Color(0xFFAA82D4),
        borderColor = Color(0xFFB8960C)
    ),
    LEGENDARIA(
        serverKey = "legendaria",
        displayLabel = "Legendaria BMSCard",
        cardColor = Color(0xFFCC6B00),
        cardColorLight = Color(0xFFFFCC44),
        borderColor = Color(0xFFFFD700)
    ),
    ULTRA_RARA(
        serverKey = "ultra_rara",
        displayLabel = "Ultra Rara BMSCard",
        cardColor = Color(0xFFB71C1C),
        cardColorLight = Color(0xFFEF9A9A),
        borderColor = Color(0xFFFF5252)
    ),
    SUPREMA(
        serverKey = "suprema",
        displayLabel = "Suprema BMSCard",
        cardColor = Color(0xFF1A0A3C),
        cardColorLight = Color(0xFFCE93D8),
        borderColor = Color(0xFFE0E0E0)
    );

    companion object {
        fun fromServerKey(key: String): BMSRarity =
            entries.firstOrNull { it.serverKey == key } ?: COMUN
    }
}

fun CollectibleCard.toBMSCardData(description: String = ""): BMSCardData = BMSCardData(
    id = id.toString(),
    title = name,
    description = description,
    rarity = when (rarity) {
        CardRarity.COMMON -> BMSRarity.COMUN
        CardRarity.EPIC -> BMSRarity.EPICA
        CardRarity.LEGENDARY -> BMSRarity.LEGENDARIA
    },
    imageUrl = imageUrl.takeIf { it.isNotBlank() }
)
