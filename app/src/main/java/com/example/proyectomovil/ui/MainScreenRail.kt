package com.example.proyectomovil.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.proyectomovil.ui.pantallas.PantallaCrearNota
import com.example.proyectomovil.ui.pantallas.PantallaCrearTarea
import com.example.proyectomovil.ui.pantallas.PantallaPrincipal
import com.example.proyectomovil.ui.model.BottomNavItem

@Composable
fun MainScreenRail(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Inicio", Icons.Default.List, "pantalla_principal"),
        BottomNavItem("Nueva Nota", Icons.Default.Add, "pantalla_crear_nota"),
        BottomNavItem("Nueva Tarea", Icons.Default.Check, "pantalla_crear_tarea")
    )

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 🔹 Barra lateral (NavigationRail)
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationRail {
                items.forEach { item ->
                    NavigationRailItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = "pantalla_principal",
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                composable("pantalla_principal") { PantallaPrincipal(navController) }
                composable("pantalla_crear_nota") { PantallaCrearNota(navController) }
                composable("pantalla_crear_tarea") { PantallaCrearTarea(navController) }
            }
        }
    }
}