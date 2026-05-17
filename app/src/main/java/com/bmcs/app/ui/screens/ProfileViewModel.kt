package com.bmcs.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmcs.app.ui.cards.LastPackState
import com.bmcs.app.ui.screens.api.ProfileRepository
import com.bmcs.app.ui.screens.api.UsuarioPerfilOut
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val perfil: UsuarioPerfilOut? = null,
    val sobresDisponibles: Int = 0,
    val notificacionesNoLeidas: Int = 0,
    val puntosAcumulados: Int = 2450,
    val error: String? = null
)

class ProfileViewModel(
    private val repository: ProfileRepository = ProfileRepository(),
    private val usuarioId: Int = DEFAULT_USUARIO_ID
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            LastPackState.totalPoints.collect { pts ->
                _uiState.value = _uiState.value.copy(puntosAcumulados = pts)
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val snapshot = repository.loadProfile(usuarioId)
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    perfil = snapshot.perfil,
                    sobresDisponibles = snapshot.sobresDisponibles,
                    notificacionesNoLeidas = snapshot.notificacionesNoLeidas,
                    puntosAcumulados = _uiState.value.puntosAcumulados,
                    error = null
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = t.message ?: "Error al cargar el perfil"
                )
            }
        }
    }

    companion object {
        const val DEFAULT_USUARIO_ID = 1
    }
}
