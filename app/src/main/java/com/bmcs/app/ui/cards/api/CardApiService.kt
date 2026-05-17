package com.bmcs.app.ui.cards.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 10.0.2.2 reaches the host machine's localhost from an Android emulator
private const val BASE_URL = "http://10.0.2.2:8000/"

interface CardApiService {

    @GET("sobres/elegibilidad")
    suspend fun getEligibility(
        @Query("usuario_id") usuarioId: Int
    ): EligibilidadOut

    @GET("sobres/")
    suspend fun getSobres(
        @Query("usuario_id") usuarioId: Int,
        @Query("solo_disponibles") soloDisponibles: Boolean = true
    ): List<SobreOut>

    @POST("sobres/{sobre_id}/abrir")
    suspend fun abrirSobre(
        @Path("sobre_id") sobreId: Int,
        @Query("usuario_id") usuarioId: Int
    ): SobreAbierto
}

object RetrofitClient {
    val cardApiService: CardApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CardApiService::class.java)
    }
}
