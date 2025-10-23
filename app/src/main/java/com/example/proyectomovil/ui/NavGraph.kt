package com.example.proyectomovil.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectomovil.ui.pantallas.PantallaCrearNota
import com.example.proyectomovil.ui.pantallas.PantallaCrearTarea
import com.example.proyectomovil.ui.pantallas.PantallaPrincipal

sealed class Pantalla(val ruta: String) {
    object Principal : Pantalla("principal")
    object CrearNota : Pantalla("crear_nota")
    object CrearTarea : Pantalla("crear_tarea")
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Pantalla.Principal.ruta) {
        composable(Pantalla.Principal.ruta) { PantallaPrincipal(navController ) }
        composable(Pantalla.CrearNota.ruta) { PantallaCrearNota(navController) }
        composable(Pantalla.CrearTarea.ruta) { PantallaCrearTarea(navController) }
    }
}
