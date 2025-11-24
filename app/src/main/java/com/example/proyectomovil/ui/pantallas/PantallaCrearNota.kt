package com.example.proyectomovil.ui.pantallas

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.proyectomovil.ui.ViewModel.nota.NotaUiState
import com.example.proyectomovil.ui.ViewModel.nota.NotaViewModel
import com.example.proyectomovil.ui.hasPermission
import com.example.proyectomovil.ui.rememberPermissionLauncher
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
            onGuardarClick = {
                viewModel.guardarNota()
                navController.popBackStack()
            },
            onRemoverArchivo = viewModel::removerArchivoUri
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoCrearNota(
    modifier: Modifier,
    notaUiState: NotaUiState,
    onValueChange: (NotaUiState) -> Unit,
    onGuardarClick: () -> Unit,
    onRemoverArchivo: (String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(hasPermission(context, Manifest.permission.CAMERA)) }
    val permissionLauncher = rememberPermissionLauncher { hasCameraPermission = it }

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let { onValueChange(notaUiState.copy(fotoUri = it.toString())) }
            }
        }
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            val currentUris = notaUiState.archivosUri.toMutableList()
            uris.forEach { uri ->
                try {
                    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                    currentUris.add(uri.toString())
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
            onValueChange(notaUiState.copy(archivosUri = currentUris))
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        if (!notaUiState.fotoUri.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(notaUiState.fotoUri),
                contentDescription = "Imagen de la nota",
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }

        if (notaUiState.archivosUri.isNotEmpty()) {
            Text("Archivos adjuntos:")
            notaUiState.archivosUri.forEach {
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

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        val newImageUri = createImageUri(context)
                        tempImageUri = newImageUri
                        cameraLauncher.launch(newImageUri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Foto")
            }

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
            modifier = Modifier.fillMaxWidth(),
            enabled = notaUiState.titulo.isNotBlank()
        ) {
            Text(stringResource(id = R.string.boton_guardar))
        }
    }
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
