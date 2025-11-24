package com.example.proyectomovil.ui.pantallas

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectomovil.R
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaUiState
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaViewModel
import com.example.proyectomovil.ui.hasPermission
import com.example.proyectomovil.ui.rememberPermissionLauncher
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearTarea(navController: NavController, tareaId: Int?) {
    val viewModel: TareaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val tareaUiState = viewModel.tareaUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleRes = if (tareaId != null) R.string.actualizar_tarea_titulo_pantalla else R.string.crear_tarea_titulo_pantalla
                    Text(stringResource(id = titleRes))
                },
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
            },
            onRemoverArchivo = viewModel::removerArchivoUri
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoCrearTarea(
    modifier: Modifier,
    tareaUiState: TareaUiState,
    onValueChange: (TareaUiState) -> Unit,
    onGuardarClick: () -> Unit,
    onRemoverArchivo: (String) -> Unit
) {
    val context = LocalContext.current

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }
    var fechaSeleccionadaTemp by remember { mutableStateOf<Long?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = tareaUiState.fechaRecordatorio ?: System.currentTimeMillis()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().get(Calendar.MINUTE)
    )

    val notificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SCHEDULE_EXACT_ALARM)
    } else {
        arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
    }
    var hasNotificationPermission by remember { mutableStateOf(notificationPermissions.all { hasPermission(context, it) }) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasNotificationPermission = permissions.values.all { it }
            if (hasNotificationPermission) {
                mostrarDatePicker = true
            }
        }
    )

    if (mostrarTimePicker) {
        AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            title = { Text("Seleccionar Hora") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val calendarioFinal = Calendar.getInstance().apply {
                            fechaSeleccionadaTemp?.let { timeInMillis = it }
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onValueChange(tareaUiState.copy(fechaRecordatorio = calendarioFinal.timeInMillis))
                        mostrarTimePicker = false
                    }
                ) { Text(stringResource(id = R.string.boton_aceptar)) }
            },
            dismissButton = {
                Button(onClick = { mostrarTimePicker = false }) { Text(stringResource(id = R.string.boton_cancelar)) }
            }
        )
    }

    var hasCameraPermission by remember { mutableStateOf(hasPermission(context, Manifest.permission.CAMERA)) }
    val cameraPermissionLauncher = rememberPermissionLauncher { hasCameraPermission = it }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { tempImageUri?.let { onValueChange(tareaUiState.copy(fotoUri = it.toString())) } }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val currentUris = tareaUiState.archivosUri.toMutableList()
        uris.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                currentUris.add(uri.toString())
            } catch (e: SecurityException) { e.printStackTrace() }
        }
        onValueChange(tareaUiState.copy(archivosUri = currentUris))
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
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
                value = tareaUiState.fechaRecordatorio?.let { formatTimestampToDateTime(it) } ?: "Seleccionar fecha y hora",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.campo_fecha)) },
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Box(modifier = Modifier.matchParentSize().clickable {
                if (hasNotificationPermission) mostrarDatePicker = true else notificationPermissionLauncher.launch(notificationPermissions)
            })
        }

        if (!tareaUiState.fotoUri.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(tareaUiState.fotoUri),
                contentDescription = "Imagen de la tarea",
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }

        if (tareaUiState.archivosUri.isNotEmpty()) {
            Text("Archivos adjuntos:")
            tareaUiState.archivosUri.forEach {
                val uriString = it
                val fileName = getFileName(context, Uri.parse(uriString))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(fileName ?: uriString, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemoverArchivo(uriString) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remover archivo")
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        val newImageUri = createImageUri(context)
                        tempImageUri = newImageUri
                        cameraLauncher.launch(newImageUri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tomar foto")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Adjuntar")
            }
        }

        Button(
            onClick = onGuardarClick,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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
                        datePickerState.selectedDateMillis?.let {
                            val cal = Calendar.getInstance().apply { timeInMillis = it }
                            cal.set(Calendar.HOUR_OF_DAY, 0)
                            cal.set(Calendar.MINUTE, 0)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            fechaSeleccionadaTemp = cal.timeInMillis

                            mostrarDatePicker = false
                            mostrarTimePicker = true
                        }
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

private fun formatTimestampToDateTime(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
}

private fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }
    return fileName
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.getExternalFilesDir("images")
    val image = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", image)
}
