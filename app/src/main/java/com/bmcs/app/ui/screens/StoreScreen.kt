package com.bmcs.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmcs.app.R
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
            StoreTopBar()
            SearchBarSection()

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MultiplicadorBanner()
                SobresRecompensasSection()

                Text(
                    text = "Canjea tus Puntos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                RewardVoucherCard(
                    voucherLabel = "Vale de Consumo",
                    subtitle = "Hauscenter",
                    imageRes = R.drawable.logo_hauscenter,
                    options = listOf(
                        VoucherOption("h1", "Bs 100", "1,550 pts"),
                        VoucherOption("h2", "Bs 150", "2,300 pts"),
                        VoucherOption("h3", "Bs 200", "3,050 pts")
                    )
                )

                RewardVoucherCard(
                    voucherLabel = "Vale de Consumo",
                    subtitle = "Farmacias Chávez",
                    imageRes = R.drawable.logo_chavez,
                    options = listOf(
                        VoucherOption("c1", "Bs 10", "200 pts"),
                        VoucherOption("c2", "Bs 50", "850 pts"),
                        VoucherOption("c3", "Bs 100", "1,550 pts")
                    )
                )

                ProbabilidadesSection()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class VoucherOption(
    val id: String,
    val amount: String,
    val points: String
)

@Composable
fun RewardVoucherCard(
    voucherLabel: String,
    subtitle: String,
    imageRes: Int,
    options: List<VoucherOption>
) {
    var selectedId by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundLight)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = voucherLabel, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = TextTertiary)

            Spacer(modifier = Modifier.height(12.dp))

            options.forEach { option ->
                val isSelected = selectedId == option.id
                val borderColor by animateColorAsState(if (isSelected) OrangePrimary else DividerColor, label = "border")
                val elevation by animateFloatAsState(if (isSelected) 4f else 0f, label = "elevation")

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedId = option.id },
                    shape = RoundedCornerShape(10.dp),
                    border = borderSize(isSelected),
                    color = if (isSelected) OrangePrimary.copy(alpha = 0.05f) else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = option.amount, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text(text = option.points, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PointsAmber)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Acción */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedId != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(text = "CANJEAR", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun borderSize(selected: Boolean) = androidx.compose.foundation.BorderStroke(
    width = if (selected) 2.dp else 1.dp,
    color = if (selected) OrangePrimary else DividerColor
)

@Composable
private fun StoreTopBar() {
    Surface(color = SurfaceWhite, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.logo_msc),
                    contentDescription = "Logo MSC",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text("Tienda MSC", fontWeight = FontWeight.Bold)
            }
            Text("2,450 pts", color = Color(0xFFDAA520), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchBarSection() {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp)) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)) {
            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Buscar premios...", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun MultiplicadorBanner() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.background(Brush.linearGradient(colors = listOf(Color(0xFF0D47A1), Color(0xFF00897B)))).padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Multiplicador x2 Activo", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Gana el doble de puntos hoy.", color = Color.White.copy(0.8f), fontSize = 12.sp)
            }
            Icon(Icons.Filled.FlashOn, null, tint = Color.Yellow)
        }
    }
}

@Composable
private fun SobresRecompensasSection() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Sobres de Recompensas", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                repeat(3) { Text("🎁", fontSize = 32.sp) }
            }
        }
    }
}

@Composable
private fun ProbabilidadesSection() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.AutoMirrored.Outlined.HelpOutline, null, tint = Color.Gray)
            Text("Cada artículo tiene su propia probabilidad de canje.", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
