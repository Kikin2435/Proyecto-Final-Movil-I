package com.example.proyectomovil.ui.pantallas

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomovil.ui.AppViewModelProvider
import com.example.proyectomovil.ui.ViewModel.settings.SettingsViewModel

@Composable
fun PantallaConfiguracion(navController: NavController) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        LanguageSettings(
            selectedLanguage = uiState.language,
            onLanguageChange = { viewModel.changeLanguage(it) }
        )

        Divider()


        FontSizeSettings(
            selectedSize = uiState.fontSize,
            onSizeChange = { viewModel.changeFontSize(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSettings(selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val languages = mapOf("es" to "Español", "en" to "English")

    Column {
        Text("Idioma de la Aplicación", style = MaterialTheme.typography.titleMedium)
        Text("Requiere reiniciar la aplicación para aplicar los cambios.", style = MaterialTheme.typography.bodySmall)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = languages[selectedLanguage] ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languages.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onLanguageChange(code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun FontSizeSettings(selectedSize: String, onSizeChange: (String) -> Unit) {
    val sizes = listOf("pequeño", "medio", "grande")

    Column {
        Text("Tamaño de Fuente", style = MaterialTheme.typography.titleMedium)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sizes.forEach { size ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedSize == size,
                        onClick = { onSizeChange(size) }
                    )
                    Text(text = size.replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}
