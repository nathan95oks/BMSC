package com.bmcs.app.ui.screens.api

data class UsuarioPerfilOut(
    val id: Int,
    val nombre: String,
    val foto_url: String?,
    val puntos_acumulados: Int,
    val cuenta_ahorros_saldo: Double,
    val moneda: String,
    val miembro_desde: String?,
    val nivel: String?
)

data class NotificacionesResumenOut(
    val no_leidas: Int
)
