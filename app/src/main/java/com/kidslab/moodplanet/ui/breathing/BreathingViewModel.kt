package com.kidslab.moodplanet.ui.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

enum class BreathingPhase(val labelResKey: String, val seconds: Int) {
    INHALE("breathing_inhale", 4),
    HOLD("breathing_hold", 2),
    EXHALE("breathing_exhale", 4)
}

private val PATTERN = listOf(BreathingPhase.INHALE, BreathingPhase.HOLD, BreathingPhase.EXHALE)
private const val TOTAL_ROUNDS = 3

data class BreathingUiState(
    val round: Int = 1,
    val phase: BreathingPhase = BreathingPhase.INHALE,
    val secondsRemainingInPhase: Int = BreathingPhase.INHALE.seconds,
    val isFinished: Boolean = false,
    val isRunning: Boolean = false
)

class BreathingViewModel(
    private val toolRepository: ToolRepository,
    private val badgeRepository: BadgeRepository,
    private val relatedEmotionEntryId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreathingUiState())
    val uiState: StateFlow<BreathingUiState> = _uiState.asStateFlow()

    private var job: Job? = null
    private var totalElapsedSeconds = 0

    fun start() {
        if (_uiState.value.isRunning) return
        _uiState.value = BreathingUiState(isRunning = true)
        totalElapsedSeconds = 0
        job = viewModelScope.launch {
            for (round in 1..TOTAL_ROUNDS) {
                for (phase in PATTERN) {
                    _uiState.value = _uiState.value.copy(
                        round = round,
                        phase = phase,
                        secondsRemainingInPhase = phase.seconds
                    )
                    var remaining = phase.seconds
                    while (isActive && remaining > 0) {
                        delay(1000)
                        remaining--
                        totalElapsedSeconds++
                        _uiState.value = _uiState.value.copy(secondsRemainingInPhase = remaining)
                    }
                }
            }
            finish()
        }
    }

    private suspend fun finish() {
        toolRepository.logSession(
            copingToolId = SeedData.ToolIds.BREATHING,
            durationSeconds = totalElapsedSeconds,
            relatedEmotionEntryId = relatedEmotionEntryId
        )
        badgeRepository.evaluateAndAward()
        _uiState.value = _uiState.value.copy(isFinished = true, isRunning = false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
