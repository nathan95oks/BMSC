package com.bmcs.app.ui.screens.api

import com.bmcs.app.ui.cards.api.SobreOut
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "http://10.0.2.2:8000/"

interface ProfileApiService {

    @GET("usuarios/{usuario_id}/perfil")
    suspend fun getPerfil(
        @Path("usuario_id") usuarioId: Int
    ): UsuarioPerfilOut

    @GET("usuarios/{usuario_id}/notificaciones/resumen")
    suspend fun getNotificacionesResumen(
        @Path("usuario_id") usuarioId: Int
    ): NotificacionesResumenOut

    @GET("sobres/")
    suspend fun getSobres(
        @Query("usuario_id") usuarioId: Int,
        @Query("solo_disponibles") soloDisponibles: Boolean = true
    ): List<SobreOut>
}

object ProfileRetrofitClient {
    val profileApiService: ProfileApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileApiService::class.java)
    }
}
