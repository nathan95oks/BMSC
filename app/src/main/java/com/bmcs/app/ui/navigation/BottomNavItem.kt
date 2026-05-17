package com.bmcs.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Store
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Album : BottomNavItem(
        route = "album",
        label = "Álbum",
        selectedIcon = Icons.Filled.CollectionsBookmark,
        unselectedIcon = Icons.Outlined.CollectionsBookmark
    )

    data object Tienda : BottomNavItem(
        route = "tienda",
        label = "Tienda",
        selectedIcon = Icons.Filled.Store,
        unselectedIcon = Icons.Outlined.Store
    )

    data object Perfil : BottomNavItem(
        route = "perfil",
        label = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
