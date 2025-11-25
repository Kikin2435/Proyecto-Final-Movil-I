package com.example.proyectomovil.ui.notificaciones

import com.example.proyectomovil.data.model.Tarea

/**
 * Define un contrato para programar y cancelar alarmas.
 * Este es el "contrato" que nuestras clases de programación de alarmas deben seguir.
 */
interface AlarmaScheduler {
    /**
     * Programa una alarma para la tarea especificada.
     */
    fun schedule(tarea: Tarea)

    /**
     * Cancela cualquier alarma programada para la tarea especificada.
     */
    fun cancel(tarea: Tarea)
}
