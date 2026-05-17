package com.bmcs.app.ui.cards.api

// Matches FiguraOut from server
data class FiguraOut(
    val id: Int,
    val nombre: String,
    val rareza: String,          // "comun"|"rara"|"epica"|"legendaria"|"ultra_rara"|"suprema"
    val descripcion: String?,
    val imagen_url: String?,
    val puntos_base: Int,
    val temporada: String?
)

// Matches ContenidoSobreOut from server
data class ContenidoSobreOut(
    val figura: FiguraOut,
    val rareza_obtenida: String,
    val puntos_generados: Int
)

// Matches SobreAbierto from server
data class SobreAbierto(
    val sobre_id: Int,
    val figuras: List<ContenidoSobreOut>,
    val puntos_totales_ganados: Int,
    val nueva_probabilidad_pity: Float
)

// Matches SobreOut from server
data class SobreOut(
    val id: Int,
    val usuario_id: Int,
    val estado: String,          // "disponible" | "abierto"
    val figuras_cantidad: Int
)

// Matches ElegibilidadOut from server
data class EligibilidadOut(
    val usuario_id: Int,
    val sobres_disponibles: Int,
    val sobres_usados_mes: Int,
    val max_sobres_mes: Int,
    val saldo_promedio_mensual: Float,
    val factor_permanencia: Float,
    val factor_desgaste: Float,
    val periodo: String
)
