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
import com.example.proyectomovil.ui.ViewModel.nota.DetalleNotaViewModel
import com.example.proyectomovil.ui.ViewModel.nota.NotaUiStateDetalle
import com.example.proyectomovil.ui.model.formatTimestampToDate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleNota(
    navController: NavController
) {
    val viewModel: DetalleNotaViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiStateDetalle.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var archivoAReproducir by remember { mutableStateOf<ArchivosMultimedia?>(null) }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text(stringResource(id = R.string.dialogo_eliminar_titulo)) },
            text = { Text("¿Estás seguro de que quieres eliminar esta nota?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.eliminarNota()
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
                title = { Text(uiState.nota?.titulo ?: stringResource(id = R.string.detalle_cargando)) },
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
            uiState.nota?.let { nota ->
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
        ContenidoDetalleNota(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
            onArchivoClick = { archivoAReproducir = it }
        )
    }
}

@Composable
private fun ContenidoDetalleNota(
    uiState: NotaUiStateDetalle,
    modifier: Modifier = Modifier,
    onArchivoClick: (ArchivosMultimedia) -> Unit
) {
    val nota = uiState.nota ?: return
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            if (!nota.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(nota.fotoUri),
                    contentDescription = "Imagen de la nota",
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        }

        item { Text(text = nota.titulo, style = MaterialTheme.typography.headlineMedium) }

        val fechaFormateada = formatTimestampToDate(nota.fechaCreacion)
        item { Text(text = stringResource(id = R.string.detalle_creado_el, fechaFormateada), style = MaterialTheme.typography.labelMedium) }

        if (nota.contenido.isNotBlank()) {
            item { Text(text = nota.contenido, style = MaterialTheme.typography.bodyLarge) }
        }

        if (uiState.archivos.isNotEmpty()) {
            item { Divider() }
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
