package com.example.proyectomovil.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.R
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaUiState
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaViewModel
import com.example.proyectomovil.ui.model.formatTimestampToDate
 import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearTarea(navController: NavController, tareaId: Int?) {

    val viewModel: TareaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val tareaUiState = viewModel.tareaUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { if (tareaId != null) Text(stringResource(id = R.string.actualizar_tarea_titulo_pantalla)) else Text(stringResource(id = R.string.crear_tarea_titulo_pantalla)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.boton_regresar))
                    }
                }
            )
        }
    ) { padding ->
        ContenidoCrearTarea(
            modifier = Modifier.padding(padding),
            tareaUiState = tareaUiState,
            onValueChange = viewModel::actualizarUiState,
            onGuardarClick = {
                viewModel.guardarTarea()
                navController.popBackStack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoCrearTarea(
    modifier: Modifier,
    tareaUiState: TareaUiState,
    onValueChange: (TareaUiState) -> Unit,
    onGuardarClick: () -> Unit
) {

    var mostrarDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = tareaUiState.fechaRecordatorio ?: System.currentTimeMillis()
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = tareaUiState.titulo,
            onValueChange = { onValueChange(tareaUiState.copy(titulo = it)) },
            label = { Text(stringResource(id = R.string.campo_titulo)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tareaUiState.contenido,
            onValueChange = { onValueChange(tareaUiState.copy(contenido = it)) },
            label = { Text(stringResource(id = R.string.campo_contenido)) },
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = tareaUiState.fechaRecordatorio?.let { formatTimestampToDate(it) } ?: "Seleccionar fecha",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.campo_fecha)) },
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { mostrarDatePicker = true }
            )
        }

        Button(
            onClick = onGuardarClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = tareaUiState.titulo.isNotBlank()
        ) {
            Text(stringResource(id = R.string.boton_guardar))
        }
    }

    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val fechaSeleccionadaMillis = datePickerState.selectedDateMillis

                        if (fechaSeleccionadaMillis != null) {
                            val calendario = Calendar.getInstance()

                            calendario.timeInMillis = fechaSeleccionadaMillis

                            calendario.set(Calendar.HOUR_OF_DAY, 12)
                            calendario.set(Calendar.MINUTE, 0)
                            calendario.set(Calendar.SECOND, 0)
                            calendario.set(Calendar.MILLISECOND, 0)

                            onValueChange(tareaUiState.copy(fechaRecordatorio = calendario.timeInMillis))
                        }

                        mostrarDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.boton_aceptar)) }
            },
            dismissButton = {
                Button(onClick = { mostrarDatePicker = false }) { Text(stringResource(id = R.string.boton_cancelar)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

}

