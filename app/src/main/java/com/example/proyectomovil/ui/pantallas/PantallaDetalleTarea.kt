package com.example.proyectomovil.ui.pantallas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectomovil.R
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.Pantalla
import com.example.proyectomovil.ui.ViewModel.Tarea.DetalleTareaViewModel
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaUiStateDetalle
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleTarea(
    navController: NavController
) {
    val viewModel: DetalleTareaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiStateDetalle.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var archivoAReproducir by remember { mutableStateOf<ArchivosMultimedia?>(null) }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text(stringResource(id = R.string.dialogo_eliminar_titulo)) },
            text = { Text(stringResource(id = R.string.dialogo_eliminar_texto)) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.eliminarTarea()
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.boton_eliminar))
                }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogoEliminar = false }) {
                    Text(stringResource(id = R.string.boton_cancelar))
                }
            }
        )
    }

    archivoAReproducir?.let {
        MediaPlayerDialog(uri = Uri.parse(it.uri), onDismiss = { archivoAReproducir = null })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.tarea?.titulo ?: stringResource(id = R.string.detalle_cargando)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.boton_regresar))
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoEliminar = true }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.boton_eliminar))
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
        ContenidoDetalleTarea(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onArchivoClick = { archivoAReproducir = it }
        )
    }
}

@Composable
private fun ContenidoDetalleTarea(
    uiState: TareaUiStateDetalle,
    modifier: Modifier = Modifier,
    onArchivoClick: (ArchivosMultimedia) -> Unit
) {
    val tarea = uiState.tarea ?: return
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (!tarea.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(tarea.fotoUri),
                    contentDescription = "Imagen de la tarea",
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }

        item { Text(text = tarea.titulo, style = MaterialTheme.typography.headlineMedium) }

        if (tarea.contenido.isNotBlank()) {
            item { Text(text = tarea.contenido, style = MaterialTheme.typography.bodyLarge) }
        }

        item { Divider() }

        if (tarea.fechasRecordatorio.isNotEmpty()) {
            item { Text(text = "Recordatorios:", style = MaterialTheme.typography.titleMedium) }
            items(tarea.fechasRecordatorio) { fecha ->
                val fechaFormateada = formatTimestampToDateTime(fecha)
                Text(text = fechaFormateada, style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (uiState.archivos.isNotEmpty()) {
            item { Text(text = "Archivos adjuntos:", style = MaterialTheme.typography.titleMedium) }

            items(uiState.archivos) { archivo ->
                val uri = Uri.parse(archivo.uri)
                val fileName = getFileName(context, uri)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (archivo.tipo == "VIDEO" || archivo.tipo == "AUDIO") {
                                onArchivoClick(archivo)
                            } else {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, context.contentResolver.getType(uri))
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Abrir con"))
                            }
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (archivo.tipo) {
                        "VIDEO" -> Icons.Default.PlayCircle
                        "AUDIO" -> Icons.Default.PlayCircleOutline
                        else -> Icons.Default.Description
                    }
                    Icon(icon, contentDescription = "Archivo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(fileName ?: uri.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
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

private fun formatTimestampToDateTime(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
}
