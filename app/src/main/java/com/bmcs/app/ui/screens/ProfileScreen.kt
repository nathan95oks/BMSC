package com.bmcs.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bmcs.app.R
import com.bmcs.app.ui.screens.api.UsuarioPerfilOut
import com.bmcs.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        ProfileTopBar(
            notificacionesNoLeidas = state.notificacionesNoLeidas
        )

        when {
            state.isLoading && state.perfil == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MercantilGreen)
                }
            }
            state.error != null && state.perfil == null -> {
                ProfileErrorState(
                    message = state.error ?: "",
                    onRetry = { viewModel.refresh() }
                )
            }
            else -> {
                val perfil = state.perfil
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    UserAvatarSection(perfil = perfil)

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedCardEntry(delayMs = 0) {
                            PuntosAcumuladosCard(puntos = 2450)
                        }

                        AnimatedCardEntry(delayMs = 80) {
                            SobresDisponiblesCard(cantidad = state.sobresDisponibles)
                        }

                        AnimatedCardEntry(delayMs = 160) {
                            CuentaAhorrosCard(
                                saldo = perfil?.cuenta_ahorros_saldo ?: 0.0,
                                moneda = perfil?.moneda ?: "Bs"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(notificacionesNoLeidas: Int) {
    Surface(
        color = SurfaceWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_msc),
                    contentDescription = "Logo MSC",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Mercantil Rewards",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            NotificationsButton(
                noLeidas = notificacionesNoLeidas,
                onClick = { /* TODO: abrir pantalla de notificaciones */ }
            )
        }
    }
}

@Composable
private fun NotificationsButton(
    noLeidas: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notificaciones",
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
        if (noLeidas > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (noLeidas > 9) "9+" else noLeidas.toString(),
                    color = TextWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UserAvatarSection(perfil: UsuarioPerfilOut?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .border(3.dp, MercantilGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val fotoUrl = perfil?.foto_url
                if (!fotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = fotoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEvents,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(40.dp),
                        tint = MercantilGreen
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(ScoreGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Verificado",
                    modifier = Modifier.size(16.dp),
                    tint = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = perfil?.nombre ?: "—",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary
        )
    }
}

@Composable
private fun PuntosAcumuladosCard(puntos: Int) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "puntosPressScale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (pressed) 6f else 2f,
        label = "puntosPressElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientOrangeStart, GradientOrangeEnd)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Puntos Acumulados",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${formatNumber(puntos)} PTS",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = (-0.5).sp
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Stars,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun SobresDisponiblesCard(cantidad: Int) {
    InfoCard(
        title = "Sobres Disponibles",
        value = cantidad.toString(),
        valueSuffix = if (cantidad == 1) " sobre" else " sobres",
        icon = Icons.Outlined.Inventory2,
        accent = MercantilGreen
    )
}

@Composable
private fun CuentaAhorrosCard(saldo: Double, moneda: String) {
    InfoCard(
        title = "Cuenta de Ahorros",
        value = "$moneda ${formatMoney(saldo)}",
        valueSuffix = null,
        icon = Icons.Outlined.AccountBalanceWallet,
        accent = MercantilGreen
    )
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    valueSuffix: String?,
    icon: ImageVector,
    accent: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "infoCardPressScale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (pressed) 4f else 1f,
        label = "infoCardPressElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    if (!valueSuffix.isNullOrBlank()) {
                        Text(
                            text = valueSuffix,
                            fontSize = 14.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No se pudo cargar el perfil",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 13.sp,
            color = TextTertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MercantilGreen)
        ) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun AnimatedCardEntry(
    delayMs: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 320)) +
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(durationMillis = 320)
                )
    ) {
        content()
    }
}

private fun formatNumber(value: Int): String =
    NumberFormat.getNumberInstance(Locale("es", "BO")).format(value)

private fun formatMoney(value: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale("es", "BO"))
    nf.minimumFractionDigits = 2
    nf.maximumFractionDigits = 2
    return nf.format(value)
}
