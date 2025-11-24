package com.example.proyectomovil.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectomovil.ui.pantallas.PantallaConfiguracion
import com.example.proyectomovil.ui.pantallas.PantallaCrearNota
import com.example.proyectomovil.ui.pantallas.PantallaCrearTarea
import com.example.proyectomovil.ui.pantallas.PantallaPrincipal

sealed class Pantalla(val ruta: String) {
    object Principal : Pantalla("pantalla_principal")
    object CrearNota : Pantalla("pantalla_crear_nota?notaId={notaId}") {
        fun crear() = "pantalla_crear_nota"

        fun editar(id: Int) = "pantalla_crear_nota?notaId=$id"
    }
    object CrearTarea : Pantalla("pantalla_crear_tarea?tareaId={tareaId}"){
        fun editar(id: Int) = "pantalla_crear_tarea?tareaId=$id"
        fun crear() = "pantalla_crear_tarea"

    }
    object DetalleNota : Pantalla("detalle_nota/{notaId}"){
        fun conId(id: Int) = "detalle_nota/$id"
    }
    object DetalleTarea : Pantalla("detalle_tarea/{tareaId}"){
        fun conId(id: Int) = "detalle_tarea/$id"
    }
    object Configuracion : Pantalla("pantalla_configuracion")

    // rutas para insertar los archivos
    object AgregarArchivo: Pantalla("agregar_archivo")
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Pantalla.Principal.ruta) {
        composable(Pantalla.Principal.ruta) { PantallaPrincipal(navController ) }
        composable(
            route = Pantalla.CrearNota.ruta,
            arguments = listOf(
                navArgument("notaId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val notaId = backStackEntry.arguments?.getInt("notaId")
            PantallaCrearNota(
                navController = navController,
                notaId = if (notaId != null && notaId != -1) notaId else null
            )
        }
        composable(Pantalla.CrearTarea.ruta, arguments = listOf(
            navArgument("tareaId"){
                type = NavType.IntType
                defaultValue = -1
            }
        )) { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getInt("tareaId")
            PantallaCrearTarea(navController, tareaId = if (tareaId != null && tareaId != -1) tareaId else null) }
        composable (Pantalla.Configuracion.ruta){ PantallaConfiguracion(navController) }
    }
}
