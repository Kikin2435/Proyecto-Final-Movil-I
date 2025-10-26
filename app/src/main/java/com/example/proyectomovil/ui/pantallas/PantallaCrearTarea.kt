package com.example.proyectomovil.ui.pantallas

import android.app.TimePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaUiState
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaViewModel
// Importa las funciones de formato que creamos
import com.example.proyectomovil.ui.model.formatTimestampToTime
import com.example.proyectomovil.ui.model.formatTimestampToDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearTarea(navController: NavController) {

    val viewModel: TareaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val tareaUiState = viewModel.tareaUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear tarea") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
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
    // --- ESTADOS PARA LOS DIÁLOGOS DE FECHA Y HORA ---
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Estado del selector de fecha
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
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = tareaUiState.contenido,
            onValueChange = { onValueChange(tareaUiState.copy(contenido = it)) },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = tareaUiState.fechaRecordatorio?.let { formatTimestampToDate(it) } ?: "Seleccionar fecha",
                    onValueChange = {},
                    label = { Text("Fecha") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarDatePicker = true } // Al hacer clic, abre el diálogo de fecha
                )
            }

            // CAMPO DE HORA (NO EDITABLE)
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = tareaUiState.fechaRecordatorio?.let { formatTimestampToTime(it) } ?: "Seleccionar hora",
                    onValueChange = {},
                    label = { Text("Hora") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { mostrarTimePicker = true }
                )
            }
        }

        Button(
            onClick = onGuardarClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = tareaUiState.titulo.isNotBlank()
        ) {
            Text("Guardar")
        }
    }

    // --- LÓGICA DE LOS DIÁLOGOS (NO OCUPAN ESPACIO VISUAL) ---
    // Diálogo del Selector de Fecha
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val fechaSeleccionada = datePickerState.selectedDateMillis
                        if (fechaSeleccionada != null) {
                            onValueChange(tareaUiState.copy(fechaRecordatorio = fechaSeleccionada))
                        }
                        mostrarDatePicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                Button(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo del Selector de Hora (usa el diálogo clásico de Android)
    if (mostrarTimePicker) {
        val calendario = Calendar.getInstance()
        if (tareaUiState.fechaRecordatorio != null) {
            calendario.timeInMillis = tareaUiState.fechaRecordatorio
        }
        val horaInicial = calendario.get(Calendar.HOUR_OF_DAY)
        val minutoInicial = calendario.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, horaSeleccionada, minutoSeleccionado ->
                val calendarioActualizado = Calendar.getInstance()
                // Mantiene la fecha existente y solo actualiza la hora y minuto
                if (tareaUiState.fechaRecordatorio != null) {
                    calendarioActualizado.timeInMillis = tareaUiState.fechaRecordatorio
                }
                calendarioActualizado.set(Calendar.HOUR_OF_DAY, horaSeleccionada)
                calendarioActualizado.set(Calendar.MINUTE, minutoSeleccionado)

                onValueChange(tareaUiState.copy(fechaRecordatorio = calendarioActualizado.timeInMillis))
                mostrarTimePicker = false
            },
            horaInicial,
            minutoInicial,
            false // Usa 'false' para formato AM/PM
        ).show()
    }
}
