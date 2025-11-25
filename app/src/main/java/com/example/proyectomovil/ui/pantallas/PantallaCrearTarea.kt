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
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaUiState
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaViewModel
import com.example.proyectomovil.ui.hasPermission
import com.example.proyectomovil.util.AudioRecorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearTarea(navController: NavController, tareaId: Int?) {
    val viewModel: TareaViewModel = viewModel(factory = AppViewModelProvider.Factory)

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
            tareaUiState = viewModel.tareaUiState,
            onValueChange = viewModel::actualizarUiState,
            audioRecorder = viewModel.audioRecorder,
            onGuardarClick = {
                viewModel.guardarTarea()
                navController.popBackStack()
            },
            onRemoverArchivo = viewModel::removerArchivo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoCrearTarea(
    modifier: Modifier,
    tareaUiState: TareaUiState,
    onValueChange: (TareaUiState) -> Unit,
    audioRecorder: AudioRecorder,
    onGuardarClick: () -> Unit,
    onRemoverArchivo: (ArchivosMultimedia) -> Unit
) {
    val context = LocalContext.current

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }
    var fechaSeleccionadaTemp by remember { mutableStateOf<Long?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = tareaUiState.fechaRecordatorio ?: System.currentTimeMillis())
    val timePickerState = rememberTimePickerState(initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY), initialMinute = Calendar.getInstance().get(Calendar.MINUTE))

    val addArchivoToState = { uri: Uri?, tipo: String ->
        uri?.let {
            try {
                if (it.authority != "${context.packageName}.provider") {
                    context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val nuevoArchivo = ArchivosMultimedia(
                    uri = it.toString(),
                    tipo = tipo,
                    tareaIdAsociada = tareaUiState.id.takeIf { id -> id != 0 },
                    notaIdAsociada = null
                )
                val archivosActualizados = tareaUiState.archivos.toMutableList().apply { add(nuevoArchivo) }
                onValueChange(tareaUiState.copy(archivos = archivosActualizados))
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { tempUri?.let { onValueChange(tareaUiState.copy(fotoUri = it.toString())) } }
    }
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) { addArchivoToState(tempUri, "VIDEO") }
    }
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri -> addArchivoToState(uri, "DOCUMENTO") }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            mostrarDatePicker = true
        }
    }

    if (mostrarTimePicker) {
        AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            title = { Text("Seleccionar Hora") },
            text = { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { TimePicker(state = timePickerState) } },
            confirmButton = {
                Button(onClick = {
                    val cal = Calendar.getInstance().apply {
                        fechaSeleccionadaTemp?.let { timeInMillis = it }
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }
                    onValueChange(tareaUiState.copy(fechaRecordatorio = cal.timeInMillis))
                    mostrarTimePicker = false
                }) { Text(stringResource(id = R.string.boton_aceptar)) }
            },
            dismissButton = { Button(onClick = { mostrarTimePicker = false }) { Text(stringResource(id = R.string.boton_cancelar)) } }
        )
    }
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        fechaSeleccionadaTemp = it
                        mostrarDatePicker = false
                        mostrarTimePicker = true
                    }
                }) { Text(stringResource(id = R.string.boton_aceptar)) }
            },
            dismissButton = { Button(onClick = { mostrarDatePicker = false }) { Text(stringResource(id = R.string.boton_cancelar)) } }
        ) { DatePicker(state = datePickerState) }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(value = tareaUiState.titulo, onValueChange = { onValueChange(tareaUiState.copy(titulo = it)) }, label = { Text(stringResource(id = R.string.campo_titulo)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tareaUiState.contenido, onValueChange = { onValueChange(tareaUiState.copy(contenido = it)) }, label = { Text(stringResource(id = R.string.campo_contenido)) }, modifier = Modifier.fillMaxWidth())
        Box(modifier = Modifier.fillMaxWidth()) {
            val notificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else {
                arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
            OutlinedTextField(
                value = tareaUiState.fechaRecordatorio?.let { formatTimestampToDateTime(it) } ?: "Seleccionar fecha y hora",
                onValueChange = {},
                label = { Text(stringResource(id = R.string.campo_fecha)) },
                readOnly = true,
                trailingIcon = {
                    if (tareaUiState.fechaRecordatorio != null) {
                        IconButton(onClick = { onValueChange(tareaUiState.copy(fechaRecordatorio = null)) }) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar recordatorio")
                        }
                    } else {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            // --- CORRECCIÓN: Se elimina el `if` que impedía la edición ---
            Box(modifier = Modifier.matchParentSize().clickable { 
                if (notificationPermissions.all { hasPermission(context, it) }) {
                    mostrarDatePicker = true
                } else {
                    permissionLauncher.launch(notificationPermissions)
                }
            })
        }

        if (!tareaUiState.fotoUri.isNullOrBlank()) {
            AsyncImage(model = Uri.parse(tareaUiState.fotoUri), contentDescription = "Imagen de la tarea", modifier = Modifier.fillMaxWidth().height(200.dp))
        }
        if (tareaUiState.archivos.isNotEmpty()) {
            Text("Archivos adjuntos:")
            tareaUiState.archivos.forEach { archivo ->
                val uri = Uri.parse(archivo.uri)
                val fileName = getFileName(context, uri)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    val icon = when (archivo.tipo) {
                        "VIDEO" -> Icons.Default.Videocam
                        "AUDIO" -> Icons.Default.Mic
                        "DOCUMENTO" -> Icons.Default.AttachFile
                        else -> Icons.Default.Description
                    }
                    Icon(icon, contentDescription = archivo.tipo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(fileName ?: uri.toString(), modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemoverArchivo(archivo) }) { Icon(Icons.Default.Close, contentDescription = "Remover archivo") }
                }
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(icon = Icons.Default.CameraAlt, text = "Foto", modifier = Modifier.weight(1f)) {
                if (hasPermission(context, Manifest.permission.CAMERA)) {
                    tempUri = createMediaUri(context, ".jpg")
                    // --- CORRECCIÓN: Llamada segura para evitar errores ---
                    tempUri?.let { cameraLauncher.launch(it) }
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }
            }
            ActionButton(icon = Icons.Default.Videocam, text = "Video", modifier = Modifier.weight(1f)) {
                val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                if (permissions.all { hasPermission(context, it) }) {
                    tempUri = createMediaUri(context, ".mp4")
                    // --- CORRECCIÓN: Llamada segura para evitar errores ---
                    tempUri?.let { videoLauncher.launch(it) }
                } else {
                    permissionLauncher.launch(permissions)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!isRecording) {
                ActionButton(icon = Icons.Default.Mic, text = "Grabar", modifier = Modifier.weight(1f)) {
                    if (hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
                        isRecording = true
                        val newAudioFile = createMediaFile(context, ".3gp")
                        audioFile = newAudioFile
                        audioRecorder.start(newAudioFile)
                    } else {
                        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                    }
                }
            } else {
                ActionButton(icon = Icons.Default.Stop, text = "Detener", modifier = Modifier.weight(1f)) {
                    audioRecorder.stop()
                    isRecording = false
                    audioFile?.let { file ->
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        addArchivoToState(uri, "AUDIO")
                    }
                }
            }
            ActionButton(icon = Icons.Default.AttachFile, text = "Adjuntar", modifier = Modifier.weight(1f)) {
                filePickerLauncher.launch(arrayOf("*/*"))
            }
        }

        Button(onClick = onGuardarClick, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), enabled = tareaUiState.titulo.isNotBlank()) {
            Text(stringResource(id = R.string.boton_guardar))
        }
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
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
            if (nameIndex != -1) fileName = it.getString(nameIndex)
        }
    }
    return fileName
}

private fun createMediaFile(context: Context, extension: String): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val mediaFileName = "${extension.uppercase(Locale.ROOT).substring(1)}_${timeStamp}_"
    val storageDir = when(extension) {
        ".jpg" -> context.getExternalFilesDir("images")
        ".mp4" -> context.getExternalFilesDir("videos")
        ".3gp" -> context.getExternalFilesDir("audio")
        else -> context.getExternalFilesDir("files")
    }
    return File.createTempFile(mediaFileName, extension, storageDir)
}

private fun createMediaUri(context: Context, extension: String): Uri {
    val file = createMediaFile(context, extension)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
