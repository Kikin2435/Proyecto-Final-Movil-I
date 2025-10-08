package com.example.proyectomovil.ui

// Clase sellada para definir todas las rutas de navegación
sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object CreateNote : Screen("crear_nota")
    object CreateTask : Screen("crear_tarea")
}
