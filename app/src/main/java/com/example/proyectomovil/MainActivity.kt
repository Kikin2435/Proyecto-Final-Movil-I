package com.example.proyectomovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.proyectomovil.data.Repository.UserPreferencesRepository
import com.example.proyectomovil.ui.MainScreen
import com.example.proyectomovil.ui.MainScreenRail
import com.example.proyectomovil.ui.theme.ProyectoMovilTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)
        lifecycleScope.launch {
            val languageCode = userPreferencesRepository.userPreferencesFlow.first().language
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
        }
        enableEdgeToEdge()
        setContent {
            ProyectoMovilTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                val navController = rememberNavController()

                when (windowSizeClass.widthSizeClass) {
                    // PANTALLA GRANDE
                    WindowWidthSizeClass.Compact -> {
                        MainScreen(navController)
                    }
                    // PANTALLA GRANDE
                    WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                        MainScreenRail(navController)
                    }
                    // POR SI NO
                    else -> {
                        MainScreen(navController)
                    }
                }
            }
        }
    }
}
