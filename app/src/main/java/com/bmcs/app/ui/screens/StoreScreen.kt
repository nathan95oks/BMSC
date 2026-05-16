package com.bmcs.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmcs.app.ui.theme.*

@Composable
fun StoreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Header Bar
            StoreTopBar()

            // Search Bar
            SearchBarSection()

            // Content
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Multiplicador Banner
                MultiplicadorBanner()

                // Sobres de Recompensas
                SobresRecompensasSection()

                // Canjea tus Puntos Header
                Text(
                    text = "Canjea tus Puntos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Hauscenter Card
                RewardVoucherCard(
                    brandName = "HAUSCENTER",
                    brandInitial = "H",
                    brandColor = Color(0xFF0D47A1),
                    voucherLabel = "Vale de Consumo",
                    subtitle = "Hauscenter",
                    options = listOf(
                        VoucherOption("Bs 100", "1,550 pts"),
                        VoucherOption("Bs 150", "2,300 pts"),
                        VoucherOption("Bs 200", "3,050 pts")
                    )
                )

                // Farmacias Chávez Card
                RewardVoucherCard(
                    brandName = "CHÁVEZ",
                    brandInitial = "C",
                    brandColor = Color(0xFF1B5E20),
                    voucherLabel = "Vale de Consumo",
                    subtitle = "Farmacias Chávez",
                    options = listOf(
                        VoucherOption("Bs 10", "200 pts"),
                        VoucherOption("Bs 50", "850 pts"),
                        VoucherOption("Bs 100", "1,550 pts")
                    )
                )

                // Info section
                ProbabilidadesSection()

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StoreTopBar() {
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
                // Person icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MercantilGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(18.dp),
                        tint = MercantilGreen
                    )
                }
                Text(
                    text = "Tienda MSC",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
            // Points Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GoldBadge.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GoldBadge)
                    )
                    Text(
                        text = "2,450 pts",
                        color = XPBadgeGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBarSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = BackgroundLight
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Buscar premios, marcas...",
                    color = TextTertiary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun MultiplicadorBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MultiplierDarkBlue,
                            MultiplierTeal
                        )
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Multiplicador x2 Activo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gana el doble de puntos en todas\ntus compras hasta el viernes.",
                    fontSize = 12.sp,
                    color = TextWhite.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TextWhite.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FlashOn,
                    contentDescription = "Multiplicador",
                    tint = GoldBadge,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SobresRecompensasSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sobres de Recompensas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BackgroundLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            modifier = Modifier.size(14.dp),
                            tint = TextTertiary
                        )
                        Text(
                            text = "Info",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder for envelope illustration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) {
                    SobreItem(isHighlighted = it == 1)
                }
            }
        }
    }
}

@Composable
private fun SobreItem(isHighlighted: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isHighlighted) GoldBadge.copy(alpha = 0.15f)
                    else BackgroundLight
                )
                .then(
                    if (isHighlighted) Modifier.border(
                        1.5.dp,
                        GoldBadge,
                        RoundedCornerShape(12.dp)
                    )
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎁",
                fontSize = 24.sp
            )
        }
        Text(
            text = if (isHighlighted) "Especial" else "Normal",
            fontSize = 10.sp,
            color = if (isHighlighted) XPBadgeGold else TextTertiary,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

data class VoucherOption(
    val amount: String,
    val points: String
)

@Composable
fun RewardVoucherCard(
    brandName: String,
    brandInitial: String,
    brandColor: Color,
    voucherLabel: String,
    subtitle: String,
    options: List<VoucherOption>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Brand header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Brand logo placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brandColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = brandInitial,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandColor
                    )
                }
                Column {
                    Text(
                        text = brandName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandColor,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Voucher label
            Text(
                text = voucherLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextTertiary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Options
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = DividerColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.amount,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = option.points,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PointsAmber
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Canjear button
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = TextWhite
                )
            ) {
                Text(
                    text = "CANJEAR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ProbabilidadesSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = "Ayuda",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "¿Cómo funcionan las probabilidades?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mientras más ahorres, mejores premios podrás ganar. Cada artículo de la tienda tiene su propia probabilidad de canje.",
                    fontSize = 12.sp,
                    color = TextTertiary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
