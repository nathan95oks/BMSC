package com.bmcs.app.ui.screens.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

data class ProfileSnapshot(
    val perfil: UsuarioPerfilOut,
    val sobresDisponibles: Int,
    val notificacionesNoLeidas: Int
)

class ProfileRepository(
    private val service: ProfileApiService = ProfileRetrofitClient.profileApiService
) {
    suspend fun loadProfile(usuarioId: Int): ProfileSnapshot = withContext(Dispatchers.IO) {
        coroutineScope {
            val perfilDeferred = async { service.getPerfil(usuarioId) }
            val sobresDeferred = async {
                service.getSobres(usuarioId = usuarioId, soloDisponibles = true)
            }
            val notifsDeferred = async { service.getNotificacionesResumen(usuarioId) }

            ProfileSnapshot(
                perfil = perfilDeferred.await(),
                sobresDisponibles = sobresDeferred.await().size,
                notificacionesNoLeidas = notifsDeferred.await().no_leidas
            )
        }
    }
}
