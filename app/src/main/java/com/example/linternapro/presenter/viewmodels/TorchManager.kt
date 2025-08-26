package com.example.linternapro.presenter.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linternapro.core.toggleFlashlight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class TorchManager
@Inject constructor (
    private val context:Context
): ViewModel() {

    private var sosJob: Job? = null
    private val sosMode = MutableStateFlow<Boolean>(false)
    val _sosMode : StateFlow<Boolean> = sosMode
    private var musicJob: Job? = null
    val isMusicMode = MutableStateFlow(false)

    private val musicMode = MutableStateFlow<Boolean>(false)
    val _musicMode : StateFlow<Boolean> = musicMode

    private val sosPattern = listOf(
        1 to 200, 0 to 200, 1 to 200, 0 to 200, 1 to 200,  // S
        0 to 600,
        1 to 600, 0 to 200, 1 to 600, 0 to 200, 1 to 600,  // O
        0 to 600,
        1 to 200, 0 to 200, 1 to 200, 0 to 200, 1 to 200   // S
    )



    fun toggleSoS(){
        sosMode.value = !_sosMode.value

        if (_sosMode.value) {
            sosJob = viewModelScope.launch {
                while (true) {
                    for ((state, duration) in sosPattern) {
                        toggleFlashlight(context, state==1)
                        delay(duration.toLong())
                    }
                    delay(1200) // pausa entre repeticiones
                }
            }
        } else {
            sosJob?.cancel()
            toggleFlashlight(context, false) // apaga linterna
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleMusicMode() {
        musicMode.value = !musicMode.value
        if (musicMode.value) {
            musicJob = viewModelScope.launch {
                isMusicMode.emit(true)
                startMusicMode()
            }
        } else {
            musicJob?.cancel()
            toggleFlashlight(context, false)
            viewModelScope.launch { isMusicMode.emit(false) }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private suspend fun startMusicMode() {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord.startRecording()
        val buffer = ShortArray(bufferSize)

        var isOn = false
        val thresholdHigh = 1500  // ajusta según tu micrófono
        val thresholdLow = 1000   // umbral más bajo para apagar

        while (true) {
            val read = audioRecord.read(buffer, 0, buffer.size)

            // RMS = raíz cuadrada del promedio de los cuadrados
            val rms = Math.sqrt(buffer.take(read).map { it.toDouble() * it }.average())

            if (!isOn && rms > thresholdHigh) {
                toggleFlashlight(context, true)
                isOn = true
            } else if (isOn && rms < thresholdLow) {
                toggleFlashlight(context, false)
                isOn = false
            }

            delay(30)  // ajusta para más o menos sensibilidad
        }
    }



}