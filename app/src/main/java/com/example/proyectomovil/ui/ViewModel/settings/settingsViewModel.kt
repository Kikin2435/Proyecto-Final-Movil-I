package com.example.proyectomovil.ui.ViewModel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.UserPreferences
import com.example.proyectomovil.data.Repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    // Expone las preferencias de usuario como un StateFlow.
    val uiState: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = UserPreferences("es", "medio") // Valor inicial antes de que cargue DataStore
        )

    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguage(languageCode)
        }
    }

    fun changeFontSize(size: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateFontSize(size)
        }
    }
}
