package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    isLargeScreen: Boolean = false
) {
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeUiState by viewModel.homeUiState.collectAsState()

    var mostrarNotas by remember { mutableStateOf(true) }

    // Estados de selección
    var notaSeleccionada by remember { mutableStateOf<Nota?>(null) }
    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = {
                                mostrarNotas = true
                                notaSeleccionada = null
                                tareaSeleccionada = null
                            },
                            colors = if (mostrarNotas)
                                ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            else ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text(stringResource(id = R.string.tab_notas))
                        }

                        FilledTonalButton(
                            onClick = {
                                mostrarNotas = false
                                notaSeleccionada = null
                                tareaSeleccionada = null
                            },
                            colors = if (!mostrarNotas)
                                ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            else ButtonDefaults.filledTonalButtonColors()
                        ) {
                            Text(stringResource(id = R.string.tab_tareas))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        if (isLargeScreen && (notaSeleccionada != null || tareaSeleccionada != null)) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    if (mostrarNotas) {
                        items(homeUiState.listaDeNotas) { nota ->
                            NotaCard(
                                nota = nota,
                                onDeleteClick = { viewModel.eliminarNota(nota) },
                                modifier = Modifier.padding(8.dp),
                                onCardClick = { notaSeleccionada = nota },
                                onEditClick = {
                                    navController.navigate(Pantalla.CrearNota.editar(nota.id))
                                }
                            )
                        }
                    } else {
                        items(homeUiState.listaDeTareas) { tarea ->
                            TareaCard(
                                tarea = tarea,
                                onDeleteClick = { viewModel.eliminarTarea(tarea) },
                                modifier = Modifier.padding(8.dp),
                                onCardClick = { tareaSeleccionada = tarea },
                                onEditClick = {
                                    navController.navigate(Pantalla.CrearTarea.editar(tarea.id))
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    if (mostrarNotas && notaSeleccionada != null) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = notaSeleccionada!!.titulo,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Row {

                                        TextButton(
                                            onClick = {
                                                navController.navigate(
                                                    Pantalla.CrearNota.editar(notaSeleccionada!!.id)
                                                )
                                            }
                                        ) {
                                            Text(stringResource(id = R.string.boton_editar))
                                        }

                                        // 🔹 Botón Cerrar
                                        TextButton(onClick = { notaSeleccionada = null }) {
                                            Text(stringResource(id = R.string.boton_regresar))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = notaSeleccionada!!.contenido,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else if (!mostrarNotas && tareaSeleccionada != null) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = tareaSeleccionada!!.titulo,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Row {

                                        TextButton(
                                            onClick = {
                                                navController.navigate(
                                                    Pantalla.CrearTarea.editar(tareaSeleccionada!!.id)
                                                )
                                            }
                                        ) {
                                            Text(stringResource(id = R.string.boton_editar))
                                        }

                                        TextButton(onClick = { tareaSeleccionada = null }) {
                                            Text(stringResource(id = R.string.boton_regresar))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = tareaSeleccionada!!.contenido,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(contentPadding = innerPadding) {
                if (mostrarNotas) {
                    items(homeUiState.listaDeNotas) { nota ->
                        NotaCard(
                            nota = nota,
                            onDeleteClick = { viewModel.eliminarNota(nota) },
                            modifier = Modifier.padding(8.dp),
                            onCardClick = {
                                if (isLargeScreen)
                                    notaSeleccionada = nota
                                else
                                    navController.navigate(Pantalla.DetalleNota.conId(nota.id))
                            },
                            onEditClick = {
                                navController.navigate(Pantalla.CrearNota.editar(id = nota.id))
                            },
                            isLargeScreen = isLargeScreen
                        )
                    }

                } else {
                    items(homeUiState.listaDeTareas) { tarea ->
                        TareaCard(
                            tarea = tarea,
                            onDeleteClick = { viewModel.eliminarTarea(tarea) },
                            modifier = Modifier.padding(8.dp),
                            onCardClick = {
                                if (isLargeScreen)
                                    tareaSeleccionada = tarea
                                else
                                    navController.navigate(Pantalla.DetalleTarea.conId(tarea.id))
                            },
                            onEditClick = {
                                navController.navigate(Pantalla.CrearTarea.editar(tarea.id))
                            },
                            isLargeScreen = isLargeScreen
                        )
                    }

                }
            }
        }
    }
}



@Composable
fun NotaCard(
    nota: Nota,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    isLargeScreen: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp) .clickable(onClick = onCardClick)) {
            Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = nota.contenido, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isLargeScreen) {
                    Button(onClick = onEditClick) {
                        Text(stringResource(id = R.string.boton_editar))
                    }
                }
                Button(onClick = onDeleteClick) {
                    Text(stringResource(id = R.string.boton_eliminar))
                }
            }
        }
    }
}


@Composable
fun TareaCard(
    tarea: Tarea,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    isLargeScreen: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp).clickable(onClick = onCardClick)) {
            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = tarea.contenido, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isLargeScreen) {
                    Button(onClick = onEditClick) {
                        Text(stringResource(id = R.string.boton_editar))
                    }
                }
                Button(onClick = onDeleteClick) {
                    Text(stringResource(id = R.string.boton_eliminar))
                }
            }
        }
    }
}

