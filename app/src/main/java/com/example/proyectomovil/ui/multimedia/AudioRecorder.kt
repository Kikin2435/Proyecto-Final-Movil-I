package com.example.proyectomovil.util // O el paquete que prefieras

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

class AudioRecorder(
    private val context: Context
) {
    private var recorder: MediaRecorder? = null

    // Prepara el grabador para un archivo específico
    private fun createRecorder(file: File): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Versión moderna para Android 12 y superior
            MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(file).fd)
            }
        } else {
            // Versión clásica para Android 11 y inferior
            @Suppress("DEPRECATION")
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
            }
        }
    }

    // Inicia la grabación en el archivo proporcionado
    fun start(outputFile: File) {
        // Detiene cualquier grabación anterior antes de empezar una nueva
        stop()
        createRecorder(outputFile).apply {
            try {
                prepare()
                start()
                recorder = this
            } catch (e: Exception) {
                e.printStackTrace()
                // Es buena idea manejar el error, quizás con un callback
            }
        }
    }

    // Detiene la grabación actual y libera los recursos
    fun stop() {
        recorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (e: Exception) {
                // Captura excepciones si se llama a stop() en un estado inválido
                e.printStackTrace()
            }
        }
        recorder = null
    }
}
