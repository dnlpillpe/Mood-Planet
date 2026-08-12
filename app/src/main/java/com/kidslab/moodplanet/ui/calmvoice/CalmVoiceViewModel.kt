package com.kidslab.moodplanet.ui.calmvoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.audio.CalmVoiceRecorder
import com.kidslab.moodplanet.data.local.seed.SeedData
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.ToolRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class PermissionState { UNKNOWN, GRANTED, DENIED }

data class CalmVoiceUiState(
    val permissionState: PermissionState = PermissionState.UNKNOWN,
    val isActive: Boolean = false,
    val isPracticingWithoutMic: Boolean = false,
    val volumeLevel: Float = 0f,
    val elapsedSeconds: Int = 0,
    val justFinished: Boolean = false,
    val newlyEarnedBadgeIds: List<Int> = emptyList()
)

class CalmVoiceViewModel(
    private val recorder: CalmVoiceRecorder,
    private val toolRepository: ToolRepository,
    private val badgeRepository: BadgeRepository,
    private val relatedEmotionEntryId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalmVoiceUiState())
    val uiState: StateFlow<CalmVoiceUiState> = _uiState.asStateFlow()

    private var recordingJob: Job? = null
    private var timerJob: Job? = null

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(
            permissionState = if (granted) PermissionState.GRANTED else PermissionState.DENIED
        )
        if (granted) startWithMic()
    }

    fun startWithMic() {
        stopInternal()
        _uiState.value = _uiState.value.copy(
            isActive = true,
            isPracticingWithoutMic = false,
            permissionState = PermissionState.GRANTED,
            elapsedSeconds = 0
        )
        recordingJob = viewModelScope.launch {
            runCatching {
                recorder.volumeLevelFlow().collect { level ->
                    _uiState.value = _uiState.value.copy(volumeLevel = level)
                }
            }.onFailure {
                // Si el micrófono falla en tiempo real, ofrecemos la alternativa sin bloquear la app.
                _uiState.value = _uiState.value.copy(isActive = false)
            }
        }
        startTimer()
    }

    /** Alternativa sin micrófono: no requiere permiso y nunca bloquea el ejercicio. */
    fun startWithoutMic() {
        stopInternal()
        _uiState.value = _uiState.value.copy(
            isActive = true,
            isPracticingWithoutMic = true,
            elapsedSeconds = 0,
            volumeLevel = 0f
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.value = _uiState.value.copy(elapsedSeconds = _uiState.value.elapsedSeconds + 1)
            }
        }
    }

    fun finish() {
        val elapsed = _uiState.value.elapsedSeconds
        stopInternal()
        viewModelScope.launch {
            toolRepository.logSession(
                copingToolId = SeedData.ToolIds.CALM_VOICE,
                durationSeconds = elapsed,
                relatedEmotionEntryId = relatedEmotionEntryId
            )
            val newBadges = badgeRepository.evaluateAndAward()
            _uiState.value = _uiState.value.copy(
                isActive = false,
                justFinished = true,
                newlyEarnedBadgeIds = newBadges
            )
        }
    }

    private fun stopInternal() {
        recordingJob?.cancel()
        recordingJob = null
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopInternal()
    }
}
