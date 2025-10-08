package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectomovil.ui.Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notas y Tareas") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate(Pantalla.CrearNota.ruta) }, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Crear nota")
            }
            Button(onClick = { navController.navigate(Pantalla.CrearTarea.ruta) }, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Crear tarea")
            }
        }
    }
}

