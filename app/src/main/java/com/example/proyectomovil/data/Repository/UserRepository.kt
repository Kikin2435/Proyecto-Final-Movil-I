package com.example.proyectomovil.data.Repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repositorio para gestionar las preferencias del usuario.
 */
class UserPreferencesRepository(private val context: Context) {


    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val FONT_SIZE = stringPreferencesKey("font_size")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->

            val language = preferences[Keys.LANGUAGE] ?: "es"
            val fontSize = preferences[Keys.FONT_SIZE] ?: "medio"
            UserPreferences(language = language, fontSize = fontSize)
        }


    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LANGUAGE] = language
        }
    }

    suspend fun updateFontSize(fontSize: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.FONT_SIZE] = fontSize
        }
    }
}
data class UserPreferences(
    val language: String,
    val fontSize: String
)
