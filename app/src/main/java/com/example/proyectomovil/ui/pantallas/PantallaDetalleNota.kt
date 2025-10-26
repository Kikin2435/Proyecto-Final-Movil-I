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
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.Pantalla
import com.example.proyectomovil.ui.ViewModel.nota.DetalleNotaViewModel
import com.example.proyectomovil.ui.model.formatTimestampToDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleNota(
    navController: NavController
) {
    val viewModel: DetalleNotaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiStateDetalle by viewModel.uiStateDetalle.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiStateDetalle.nota?.titulo ?: stringResource(id = R.string.detalle_cargando)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.boton_regresar))
                    }
                }
            )
        },
        floatingActionButton = {
            uiStateDetalle.nota?.let { nota ->
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Pantalla.CrearNota.editar(nota.id))
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.actualizar_nota_titulo_pantalla))
                }
            }
        }
    ) { innerPadding ->
        uiStateDetalle.nota?.let { nota ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = nota.titulo, style = MaterialTheme.typography.headlineMedium)
                val fechaFormateada = formatTimestampToDate(nota.fechaCreacion)
                Text(
                    text = stringResource(id = R.string.detalle_creado_el, fechaFormateada),
                    style = MaterialTheme.typography.labelMedium
                )
                Divider()
                Text(text = nota.contenido, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
