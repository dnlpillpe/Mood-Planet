package com.kidslab.moodplanet.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.math.sqrt

/**
 * Analiza el volumen aproximado del micrófono para el ejercicio
 * "Mi voz tranquila", SIN reconocimiento de voz y SIN guardar audio.
 *
 * Cada bloque de muestras PCM se lee en memoria, se calcula su nivel RMS
 * (0f a 1f) y el bloque se descarta inmediatamente: nunca se escribe a
 * disco ni se envía a ningún servidor (la app no tiene permiso de
 * INTERNET). Ver docs/PRIVACIDAD.md.
 */
class CalmVoiceRecorder {

    companion object {
        private const val SAMPLE_RATE_HZ = 16_000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

        fun hasPermission(context: Context): Boolean =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }

    /**
     * Emite un nivel de volumen normalizado (0f = silencio, 1f = muy fuerte)
     * mientras el flujo esté siendo recolectado. Al cancelarse, libera el
     * AudioRecord de inmediato. No persiste ningún dato de audio.
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun volumeLevelFlow(): Flow<Float> = callbackFlow {
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HZ, CHANNEL_CONFIG, AUDIO_ENCODING)
        if (minBufferSize <= 0) {
            close(IllegalStateException("El dispositivo no soporta esta configuración de audio"))
            return@callbackFlow
        }

        val bufferSize = minBufferSize * 2
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE_HZ,
            CHANNEL_CONFIG,
            AUDIO_ENCODING,
            bufferSize
        )

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            close(IllegalStateException("No se pudo inicializar el micrófono"))
            return@callbackFlow
        }

        val pcmBuffer = ShortArray(bufferSize / 2)
        audioRecord.startRecording()

        // Bucle de lectura en un hilo del pool de IO administrado por callbackFlow.
        while (isActive) {
            val samplesRead = audioRecord.read(pcmBuffer, 0, pcmBuffer.size)
            if (samplesRead > 0) {
                val level = computeNormalizedRms(pcmBuffer, samplesRead)
                trySend(level)
            }
            // pcmBuffer se sobreescribe en la siguiente lectura: no se guarda en ningún lado.
        }

        awaitClose {
            runCatching {
                audioRecord.stop()
            }
            audioRecord.release()
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)

    /** Calcula el volumen RMS del bloque y lo normaliza aproximadamente a 0f..1f. */
    private fun computeNormalizedRms(buffer: ShortArray, count: Int): Float {
        var sumOfSquares = 0.0
        for (i in 0 until count) {
            val sample = buffer[i].toDouble()
            sumOfSquares += sample * sample
        }
        val rms = sqrt(sumOfSquares / count)
        // 32767 es la amplitud máxima posible de una muestra PCM de 16 bits.
        val normalized = (rms / 32767.0).toFloat()
        return normalized.coerceIn(0f, 1f)
    }
}
