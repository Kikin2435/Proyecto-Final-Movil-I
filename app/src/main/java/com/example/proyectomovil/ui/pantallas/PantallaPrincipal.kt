package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.Pantalla
import com.example.proyectomovil.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavController,
) {

    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notas y Tareas") })
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.navigate(Pantalla.CrearNota.ruta) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Crear nota")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { navController.navigate(Pantalla.CrearTarea.ruta) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Crear tarea")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(

            contentPadding = innerPadding
        ) {
            items(homeUiState.listaDeNotas) { nota ->
                NotaCard(
                    nota = nota,
                    onDeleteClick = { viewModel.eliminarNota(nota) },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun NotaCard(nota: Nota, onDeleteClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) { // Añadido fillMaxWidth para que ocupe todo el ancho
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = nota.contenido, style = MaterialTheme.typography.bodySmall)
            Button(onClick = onDeleteClick) {
                Text("Eliminar")
            }
        }
    }
}
