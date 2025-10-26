package com.example.proyectomovil.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.proyectomovil.R
import com.example.proyectomovil.ui.pantallas.PantallaCrearNota
import com.example.proyectomovil.ui.pantallas.PantallaCrearTarea
import com.example.proyectomovil.ui.pantallas.PantallaPrincipal
import com.example.proyectomovil.ui.model.BottomNavItem
import com.example.proyectomovil.ui.pantallas.PantallaConfiguracion
import com.example.proyectomovil.ui.pantallas.PantallaDetalleNota
import com.example.proyectomovil.ui.pantallas.PantallaDetalleTarea

@Composable
fun MainScreenRail(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(stringResource(id = R.string.nav_inicio), Icons.Default.List, "pantalla_principal"),
        BottomNavItem(stringResource(id = R.string.nav_nota), Icons.Default.Add, "pantalla_crear_nota"),
        BottomNavItem(stringResource(id = R.string.nav_tarea), Icons.Default.Check, "pantalla_crear_tarea"),
        BottomNavItem(stringResource(id = R.string.nav_configuracion), Icons.Default.Settings, "pantalla_configuracion")
    )

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                composable(
                    route = Pantalla.CrearNota.ruta,
                    arguments = listOf(
                        navArgument("notaId") {
                            type = NavType.IntType
                            defaultValue = -1 // -1 indica que no hay ID (modo Crear)
                        }
                    )
                ) { backStackEntry ->
                    val notaId = backStackEntry.arguments?.getInt("notaId")
                    // Asumo que PantallaCrearNota está preparada para recibir 'notaId' como Int?
                    PantallaCrearNota(
                        navController = navController,
                        notaId = if (notaId != null && notaId != -1) notaId else null
                    )
                }
                composable("pantalla_crear_tarea", arguments = listOf(
                    navArgument("tareaId"){
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )) { backStackEntry ->
                    val tareaId = backStackEntry.arguments?.getInt("tareaId")
                    PantallaCrearTarea(navController, tareaId = if (tareaId != null && tareaId != -1) tareaId else null) }


                composable("pantalla_configuracion") { PantallaConfiguracion(navController) }


                composable(
                    route = Pantalla.DetalleNota.ruta,
                    arguments = listOf(navArgument("notaId") { type = NavType.IntType })
                ) {
                    PantallaDetalleNota(navController = navController)
                }
                composable(
                    route = Pantalla.DetalleTarea.ruta,
                    arguments = listOf(navArgument("tareaId") { type = NavType.IntType })
                ) {
                    PantallaDetalleTarea(navController = navController)
                }
            }
        }
    }
}