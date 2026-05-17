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
    val description: String = "",
    val imageUrl: String,
    val rarity: CardRarity,
    val isOwned: Boolean,
    val isNew: Boolean = false
)

// ─── Datos de muestra ────────────────────────────────────────────────────────

val sampleCards: List<CollectibleCard> = listOf(
    CollectibleCard(
        id = 1, number = "001", name = "Salar de Uyuni",
        description = "El desierto de sal más grande del mundo, ubicado en el altiplano boliviano. Refleja el cielo como un espejo infinito.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3d/Salar_de_Uyuni%2C_Bol%C3%ADvia.jpg/320px-Salar_de_Uyuni%2C_Bol%C3%ADvia.jpg",
        rarity = CardRarity.LEGENDARY, isOwned = true, isNew = true
    ),
    CollectibleCard(
        id = 2, number = "002", name = "Puerta del Sol",
        description = "Monumento monolítico de la civilización Tiwanaku, tallado en un solo bloque de piedra andesita hace más de 1.500 años.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9b/Tiwanaku_gateway_of_the_sun_23jul2007.jpg/320px-Tiwanaku_gateway_of_the_sun_23jul2007.jpg",
        rarity = CardRarity.EPIC, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 3, number = "003", name = "Cóndor Andino",
        description = "Ave emblemática de los Andes con una envergadura de hasta 3,2 metros. Símbolo de poder y libertad en la cultura andina.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Condor_Quebrada_del_Condorito_-_Wikicommons.jpg/320px-Condor_Quebrada_del_Condorito_-_Wikicommons.jpg",
        rarity = CardRarity.COMMON, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 4, number = "004", name = "Lago Titicaca",
        description = "El lago navegable más alto del mundo a 3.810 msnm, compartido entre Bolivia y Perú. Cuna de la civilización inca.",
        imageUrl = "",
        rarity = CardRarity.COMMON, isOwned = false
    ),
    CollectibleCard(
        id = 5, number = "005", name = "Tiwanaku",
        description = "Centro espiritual y político de la cultura Tiwanaku que floreció entre 300 y 1000 d.C. en el altiplano boliviano.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Tiwanaku_Ponce.jpg/320px-Tiwanaku_Ponce.jpg",
        rarity = CardRarity.EPIC, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 6, number = "006", name = "Carnaval de Oruro",
        description = "Obra Maestra del Patrimonio Oral e Intangible de la Humanidad por la UNESCO. Celebración de fe, danza y color.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/CarnavalOruro.jpg/320px-CarnavalOruro.jpg",
        rarity = CardRarity.LEGENDARY, isOwned = false
    ),
    CollectibleCard(
        id = 7, number = "007", name = "Lagunas de Colores",
        description = "Lagunas altiplánicas de colores únicos causados por algas y minerales. La Laguna Colorada alberga flamencos rosados.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Laguna_Colorada_Bolivia_Luca_Galuzzi_2006.jpg/320px-Laguna_Colorada_Bolivia_Luca_Galuzzi_2006.jpg",
        rarity = CardRarity.LEGENDARY, isOwned = true, isNew = true
    ),
    CollectibleCard(
        id = 8, number = "008", name = "Mercado de las Brujas",
        description = "Mercado tradicional en La Paz donde se venden ingredientes para rituales andinos y ofrendas a la Pachamama.",
        imageUrl = "",
        rarity = CardRarity.COMMON, isOwned = false
    ),
    CollectibleCard(
        id = 9, number = "009", name = "Cholita Paceña",
        description = "Símbolo de identidad y orgullo boliviano. La cholita representa la fortaleza y elegancia de la mujer aymara.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Bolivia_chola.jpg/240px-Bolivia_chola.jpg",
        rarity = CardRarity.EPIC, isOwned = true, isNew = false
    ),
    CollectibleCard(
        id = 10, number = "010", name = "Valle de la Luna",
        description = "Formación geológica de arcilla y arena al sur de La Paz que recuerda un paisaje lunar por su erosión natural.",
        imageUrl = "",
        rarity = CardRarity.COMMON, isOwned = false
    ),
    CollectibleCard(
        id = 11, number = "011", name = "Potosí Colonial",
        description = "Ciudad Patrimonio de la Humanidad, fue una de las ciudades más grandes del mundo en el siglo XVII por su plata.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Potosi_01.jpg/320px-Potosi_01.jpg",
        rarity = CardRarity.EPIC, isOwned = false
    ),
    CollectibleCard(
        id = 12, number = "012", name = "Cerro Rico",
        description = "El 'cerro que come hombres': montaña de plata que financió el Imperio Español durante tres siglos desde 1545.",
        imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Cerro_rico_desde_potosi_con_estatua.jpg/320px-Cerro_rico_desde_potosi_con_estatua.jpg",
        rarity = CardRarity.COMMON, isOwned = true, isNew = false
    )
)
