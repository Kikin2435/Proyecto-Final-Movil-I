package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.R
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.data.model.Tarea
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.Pantalla
import com.example.proyectomovil.ui.ViewModel.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavController,
) {
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by viewModel.homeUiState.collectAsState()

    var mostrarNotas by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { mostrarNotas = true },
                            colors = if (mostrarNotas)
                                ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            else
                                ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text(stringResource(id = R.string.tab_notas))
                        }

                        FilledTonalButton(
                            onClick = { mostrarNotas = false },
                            colors = if (!mostrarNotas)
                                ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            else
                                ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text(stringResource(id = R.string.tab_tareas))
                        }
                    }
                }
            )
        },
//        bottomBar = {
//            BottomAppBar {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Button(
//                        onClick = { navController.navigate(Pantalla.CrearNota.ruta) },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Crear nota")
//                    }
//
//                    Spacer(modifier = Modifier.width(16.dp))
//
//                    Button(
//                        onClick = { navController.navigate(Pantalla.CrearTarea.ruta) },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Crear tarea")
//                    }
//                }
//            }

    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            if (mostrarNotas) {
                items(homeUiState.listaDeNotas) { nota ->
                    NotaCard(
                        nota = nota,
                        onDeleteClick = { viewModel.eliminarNota(nota) },
                        modifier = Modifier.padding(8.dp),
                        onCardClick = {
                            navController.navigate(Pantalla.DetalleNota.conId(nota.id))
                        }
                    )
                }
            } else {
                items(homeUiState.listaDeTareas) { tarea ->
                    TareaCard(
                        tarea = tarea,
                        onDeleteClick = { viewModel.eliminarTarea(tarea) },
                        modifier = Modifier.padding(8.dp),
                        onCardClick = {
                            navController.navigate(Pantalla.DetalleTarea.conId(tarea.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotaCard(nota: Nota, onDeleteClick: () -> Unit, modifier: Modifier = Modifier, onCardClick: () -> Unit) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onCardClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = nota.contenido, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDeleteClick) {
                Text(stringResource(id = R.string.boton_eliminar))
            }
        }
    }
}

@Composable
fun TareaCard(tarea: Tarea, onDeleteClick: () -> Unit, modifier: Modifier = Modifier, onCardClick: () -> Unit) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onCardClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = tarea.contenido, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDeleteClick) {
                Text(stringResource(id = R.string.boton_eliminar))
            }
        }
    }
}
