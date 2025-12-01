package com.example.proyectomovil.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream

// CLASE PARA EL MANEJO DEL AUDIO

class AudioRecorder(
    private val context: Context
) {
    private var recorder: MediaRecorder? = null


    private fun createRecorder(file: File): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(file).fd)
            }
        } else {

            @Suppress("DEPRECATION")
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
            }
        }
    }


    fun start(outputFile: File) {

        stop()
        createRecorder(outputFile).apply {
            try {
                prepare()
                start()
                recorder = this
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }


    fun stop() {
        recorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
        recorder = null
    }
}
