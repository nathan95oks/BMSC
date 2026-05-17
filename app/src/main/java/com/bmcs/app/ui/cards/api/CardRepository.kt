package com.bmcs.app.ui.cards.api

import com.bmcs.app.ui.cards.BMSCardData
import com.bmcs.app.ui.cards.BMSRarity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardRepository(
    private val service: CardApiService = RetrofitClient.cardApiService
) {
    // Opens the first available sobre for the given user and returns its cards.
    // Throws if there are no sobres disponibles or the network call fails.
    suspend fun openNextSobre(usuarioId: Int): List<BMSCardData> = withContext(Dispatchers.IO) {
        val sobres = service.getSobres(usuarioId = usuarioId, soloDisponibles = true)

        val sobre = sobres.firstOrNull()
            ?: throw NoSuchElementException("No hay sobres disponibles para el usuario $usuarioId")

        val result = service.abrirSobre(sobreId = sobre.id, usuarioId = usuarioId)

        result.figuras.take(5).map { contenido ->
            BMSCardData(
                id = contenido.figura.id.toString(),
                title = contenido.figura.nombre,
                description = contenido.figura.descripcion ?: "",
                rarity = BMSRarity.fromServerKey(contenido.rareza_obtenida),
                imageUrl = contenido.figura.imagen_url
            )
        }
    }
}
