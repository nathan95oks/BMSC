package com.bmcs.app.ui.album

import androidx.compose.ui.graphics.Color

// ─── Rareza ──────────────────────────────────────────────────────────────────

enum class CardRarity(
    val label: String,
    val points: Int,
    val borderColor: Color,
    val badgeColor: Color,
    val textColor: Color
) {
    COMMON(
        label     = "Common",
        points    = 5,
        borderColor = Color(0xFFBDCABB),
        badgeColor  = Color(0xFFF0EDED),
        textColor   = Color(0xFF3E4A3E)
    ),
    EPIC(
        label     = "Epic",
        points    = 8,
        borderColor = Color(0xFFBDC3C7),
        badgeColor  = Color(0xFF166735),
        textColor   = Color.White
    ),
    LEGENDARY(
        label     = "Legendary",
        points    = 10,
        borderColor = Color(0xFFFFD700),
        badgeColor  = Color(0xFF00843D),
        textColor   = Color.White
    )
}

// ─── Modelo de carta ─────────────────────────────────────────────────────────

data class CollectibleCard(
    val id: Int,
    val number: String,          // e.g. "001"
    val name: String,
    val imageUrl: String,
    val rarity: CardRarity,
    val isOwned: Boolean,
    val isNew: Boolean = false
)

// ─── Datos de muestra ────────────────────────────────────────────────────────

val sampleCards: List<CollectibleCard> = listOf(
    CollectibleCard(
        id = 1, number = "001", name = "Salar de Uyuni",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/Salar_de_Uyuni%2C_Bol%C3%ADvia.jpg/320px-Salar_de_Uyuni%2C_Bol%C3%ADvia.jpg",
        rarity = CardRarity.LEGENDARY, isOwned = true, isNew = true
    ),
    CollectibleCard(
        id = 2, number = "002", name = "Puerta del Sol",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9b/Tiwanaku_gateway_of_the_sun_23jul2007.jpg/320px-Tiwanaku_gateway_of_the_sun_23jul2007.jpg",
        rarity = CardRarity.EPIC, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 3, number = "003", name = "Cóndor Andino",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Condor_Quebrada_del_Condorito_-_Wikicommons.jpg/320px-Condor_Quebrada_del_Condorito_-_Wikicommons.jpg",
        rarity = CardRarity.COMMON, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 4, number = "004", name = "Lago Titicaca",
        imageUrl = "",
        rarity = CardRarity.COMMON, isOwned = false
    ),
    CollectibleCard(
        id = 5, number = "005", name = "Tiwanaku",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Tiwanaku_Ponce.jpg/320px-Tiwanaku_Ponce.jpg",
        rarity = CardRarity.EPIC, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 6, number = "006", name = "Carnaval de Oruro",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/CarnavalOruro.jpg/320px-CarnavalOruro.jpg",
        rarity = CardRarity.LEGENDARY, isOwned = false
    ),
    CollectibleCard(
        id = 7, number = "007", name = "Lagunas de Colores",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Laguna_Colorada_Bolivia_Luca_Galuzzi_2006.jpg/320px-Laguna_Colorada_Bolivia_Luca_Galuzzi_2006.jpg",
        rarity = CardRarity.LEGENDARY, isOwned = true, isNew = true
    ),
    CollectibleCard(
        id = 8, number = "008", name = "Mercado de las Brujas",
        imageUrl = "",
        rarity = CardRarity.COMMON, isOwned = false
    ),
    CollectibleCard(
        id = 9, number = "009", name = "Cholita Paceña",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Bolivia_chola.jpg/240px-Bolivia_chola.jpg",
        rarity = CardRarity.EPIC, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 10, number = "010", name = "Valle de la Luna",
        imageUrl = "",
        rarity = CardRarity.COMMON, isOwned = false
    ),
    CollectibleCard(
        id = 11, number = "011", name = "Potosí Colonial",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Potosi_01.jpg/320px-Potosi_01.jpg",
        rarity = CardRarity.EPIC, isOwned = false
    ),
    CollectibleCard(
        id = 12, number = "012", name = "Cerro Rico",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Cerro_rico_desde_potosi_con_estatua.jpg/320px-Cerro_rico_desde_potosi_con_estatua.jpg",
        rarity = CardRarity.COMMON, isOwned = true, isNew = false
    )
)
