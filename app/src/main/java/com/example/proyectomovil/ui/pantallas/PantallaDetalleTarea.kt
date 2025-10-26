package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.R
import com.example.proyectomovil.data.model.Tarea
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.Pantalla
import com.example.proyectomovil.ui.ViewModel.Tarea.DetalleTareaViewModel
import com.example.proyectomovil.ui.model.formatTimestampToDate // Reutiliza tu función de formato

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleTarea(
    navController: NavController
) {

    val viewModel: DetalleTareaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiStateDetalle.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.tarea?.titulo ?: stringResource(id = R.string.detalle_cargando)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.boton_regresar))
                    }
                }
            )
        },
        floatingActionButton = {
            uiState.tarea?.let { tarea ->
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Pantalla.CrearTarea.editar(tarea.id))
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.actualizar_tarea_titulo_pantalla))
                }
            }
        }
    ) { innerPadding ->
        uiState.tarea?.let { tarea ->
            ContenidoDetalleTarea(tarea = tarea, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun ContenidoDetalleTarea(tarea: Tarea, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = tarea.titulo,
            style = MaterialTheme.typography.headlineMedium
        )

        if (tarea.contenido.isNotBlank()) {
            Text(
                text = tarea.contenido,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Divider()

        tarea.fechaRecordatorio?.let { fecha ->
            val fechaFormateada = formatTimestampToDate(tarea.fechaRecordatorio)
            Text(
                text = stringResource(id = R.string.detalle_creado_el, fechaFormateada),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
