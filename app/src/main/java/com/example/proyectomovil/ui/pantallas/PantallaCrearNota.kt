package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.R
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.nota.NotaUiState
import com.example.proyectomovil.ui.ViewModel.nota.NotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearNota(navController: NavController, notaId: Int?) {
    val viewModel: NotaViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  if(notaId == null) Text(  stringResource(id = R.string.crear_nota_titulo_pantalla)) else Text(stringResource(id = R.string.actualizar_nota_titulo_pantalla))},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.boton_regresar))
                    }
                }
            )
        },
    ){ innerPadding ->
        ContenidoCrearNota(
            modifier = Modifier.padding(innerPadding),
            notaUiState = viewModel.notaUiState,
            onValueChange = viewModel::actualizarUiState,
            onGuardarClick = {
                viewModel.guardarNota()
                navController.popBackStack()
            }
        )
    }
}

@Composable
private fun ContenidoCrearNota(
    modifier: Modifier,
    notaUiState: NotaUiState,
    onValueChange: (NotaUiState) -> Unit,
    onGuardarClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = notaUiState.titulo,
            onValueChange = { onValueChange(notaUiState.copy(titulo = it)) },
            label = { Text(stringResource(id = R.string.campo_titulo)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = notaUiState.contenido,
            onValueChange = { onValueChange(notaUiState.copy(contenido = it)) },
            label = { Text(stringResource(id = R.string.campo_contenido)) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Button(
            onClick = onGuardarClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = notaUiState.titulo.isNotBlank()
        ) {
            Text(stringResource(id = R.string.boton_guardar))
        }
    }
}
