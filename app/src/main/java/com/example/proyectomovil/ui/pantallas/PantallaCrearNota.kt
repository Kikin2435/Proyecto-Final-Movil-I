package com.example.proyectomovil.ui.pantallas

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectomovil.R
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.nota.NotaUiState
import com.example.proyectomovil.ui.ViewModel.nota.NotaViewModel
import com.example.proyectomovil.ui.hasPermission
import com.example.proyectomovil.util.AudioRecorder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearNota(navController: NavController, notaId: Int?) {
    val viewModel: NotaViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { if (notaId == null) Text(stringResource(id = R.string.crear_nota_titulo_pantalla)) else Text(stringResource(id = R.string.actualizar_nota_titulo_pantalla)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.boton_regresar))
                    }
                }
            )
        },
    ) { innerPadding ->
        ContenidoCrearNota(
            modifier = Modifier.padding(innerPadding),
            notaUiState = viewModel.notaUiState,
            onValueChange = viewModel::actualizarUiState,
            audioRecorder = viewModel.audioRecorder,
            onGuardarClick = {
                viewModel.guardarNota()
                navController.popBackStack()
            },
            onRemoverArchivo = viewModel::removerArchivo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoCrearNota(
    modifier: Modifier,
    notaUiState: NotaUiState,
    onValueChange: (NotaUiState) -> Unit,
    audioRecorder: AudioRecorder,
    onGuardarClick: () -> Unit,
    onRemoverArchivo: (ArchivosMultimedia) -> Unit
) {
    val context = LocalContext.current

    var mostrarDialogoPermiso by remember { mutableStateOf(false) }
    var textoDialogoPermiso by remember { mutableStateOf("") }
    var archivoAReproducir by remember { mutableStateOf<Uri?>(null) }

    var accionConPermiso by remember { mutableStateOf<(() -> Unit)?>(null) }
    var permissionsRequested by remember { mutableStateOf(emptySet<String>()) }

    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            accionConPermiso?.invoke()
        }
        accionConPermiso = null
    }

    val addArchivoToState = { uri: Uri?, tipo: String ->
        uri?.let {
            try {
                if (it.authority != "${context.packageName}.provider") {
                    context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val nuevoArchivo = ArchivosMultimedia(
                    uri = it.toString(),
                    tipo = tipo,
                    notaIdAsociada = notaUiState.id.takeIf { id -> id != 0 },
                    tareaIdAsociada = null
                )
                onValueChange(notaUiState.copy(archivos = notaUiState.archivos + nuevoArchivo))
            } catch (e: SecurityException) { e.printStackTrace() }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) { tempUri?.let { onValueChange(notaUiState.copy(fotoUri = it.toString())) } }
    }
    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) { addArchivoToState(tempUri, "VIDEO") }
    }
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri -> addArchivoToState(uri, "DOCUMENTO") }
    }

    archivoAReproducir?.let {
        MediaPlayerDialog(uri = it, onDismiss = { archivoAReproducir = null })
    }

    if (mostrarDialogoPermiso) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPermiso = false },
            title = { Text("Permiso Requerido") },
            text = { Text(textoDialogoPermiso) },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    mostrarDialogoPermiso = false
                }) { Text("Ir a Ajustes") }
            },
            dismissButton = { Button(onClick = { mostrarDialogoPermiso = false }) { Text("Cancelar") } }
        )
    }

    val handlePermission = { permissions: Array<String>, permissionText: String, action: () -> Unit ->
        if (permissions.all { hasPermission(context, it) }) {
            action()
        } else {
            val activity = context.findActivity()
            val permanentlyDenied = permissions.any { perm ->
                activity?.let { !ActivityCompat.shouldShowRequestPermissionRationale(it, perm) && permissionsRequested.contains(perm) } ?: false
            }

            if (permanentlyDenied) {
                textoDialogoPermiso = "Para usar esta función, necesitas el permiso de $permissionText. Por favor, habilítalo en los ajustes de la aplicación."
                mostrarDialogoPermiso = true
            } else {
                permissionsRequested = permissionsRequested + permissions
                accionConPermiso = action
                permissionLauncher.launch(permissions)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(value = notaUiState.titulo, onValueChange = { onValueChange(notaUiState.copy(titulo = it)) }, label = { Text(stringResource(id = R.string.campo_titulo)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = notaUiState.contenido, onValueChange = { onValueChange(notaUiState.copy(contenido = it)) }, label = { Text(stringResource(id = R.string.campo_contenido)) }, modifier = Modifier.fillMaxWidth().height(120.dp))
        if (!notaUiState.fotoUri.isNullOrBlank()) {
             Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = Uri.parse(notaUiState.fotoUri),
                    contentDescription = "Imagen de la nota",
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
                IconButton(
                    onClick = { onValueChange(notaUiState.copy(fotoUri = null)) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quitar foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    )
                }
            }
        }

        if (notaUiState.archivos.isNotEmpty()) {
            Text("Archivos adjuntos:")
            notaUiState.archivos.forEach { archivo ->
                val uri = Uri.parse(archivo.uri)
                val fileName = getFileName(context, uri)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (archivo.tipo == "VIDEO" || archivo.tipo == "AUDIO") {
                                archivoAReproducir = uri
                            }
                        }
                        .padding(vertical = 4.dp)
                ) {
                    val icon = when (archivo.tipo) {
                        "VIDEO" -> Icons.Default.PlayCircle
                        "AUDIO" -> Icons.Default.PlayCircleOutline
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
                handlePermission(arrayOf(Manifest.permission.CAMERA), "Cámara") {
                    tempUri = createMediaUri(context, ".jpg")
                    tempUri?.let { cameraLauncher.launch(it) } ?: Unit
                }
            }
            ActionButton(icon = Icons.Default.Videocam, text = "Video", modifier = Modifier.weight(1f)) {
                handlePermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), "Cámara y Micrófono") {
                    tempUri = createMediaUri(context, ".mp4")
                    tempUri?.let { videoLauncher.launch(it) } ?: Unit
                }
            }
        }

        if (!isRecording) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(icon = Icons.Default.Mic, text = "Grabar", modifier = Modifier.weight(1f)) {
                    handlePermission(arrayOf(Manifest.permission.RECORD_AUDIO), "Micrófono") {
                        isRecording = true
                        val newAudioFile = createMediaFile(context, ".3gp")
                        audioFile = newAudioFile
                        audioRecorder.start(newAudioFile)
                    }
                }
                ActionButton(icon = Icons.Default.AttachFile, text = "Adjuntar", modifier = Modifier.weight(1f)) {
                    filePickerLauncher.launch(arrayOf("*/*"))
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.large)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Grabando",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Grabando...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                IconButton(onClick = {
                    audioRecorder.stop()
                    isRecording = false
                    audioFile?.delete()
                    audioFile = null
                }) {
                    Icon(Icons.Filled.Cancel, contentDescription = "Cancelar grabación", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                IconButton(onClick = {
                    audioRecorder.stop()
                    isRecording = false
                    audioFile?.let { file ->
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        addArchivoToState(uri, "AUDIO")
                    }
                }) {
                    Icon(Icons.Filled.StopCircle, contentDescription = "Detener grabación", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        Button(onClick = onGuardarClick, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), enabled = notaUiState.titulo.isNotBlank()) {
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

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
