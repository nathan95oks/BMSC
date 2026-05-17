package com.bmcs.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bmcs.app.ui.navigation.BottomNavItem
import com.bmcs.app.ui.theme.MercantilGreen
import com.bmcs.app.ui.theme.NavInactive
import com.bmcs.app.ui.theme.SurfaceWhite

@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Album,
        BottomNavItem.Tienda,
        BottomNavItem.Perfil,
        BottomNavItem.Test  // TEST — DELETE BEFORE RELEASE
    )

    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            val iconScale by animateFloatAsState(
                targetValue = if (selected) 1.3f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "scale_${item.route}"
            )

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MercantilGreen,
                    selectedTextColor = MercantilGreen,
                    unselectedIconColor = NavInactive,
                    unselectedTextColor = NavInactive,
                    indicatorColor = MercantilGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}
