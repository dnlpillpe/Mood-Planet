package com.kidslab.moodplanet.ui.toolbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.local.seed.SeedData
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.ToolRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Pasos de las mini-herramientas que se resuelven dentro de la propia Caja de herramientas. */
val groundingSteps = listOf(
    "Nombra 5 cosas que puedes VER a tu alrededor",
    "Nombra 4 cosas que puedes TOCAR",
    "Nombra 3 cosas que puedes OÍR",
    "Nombra 2 cosas que puedes OLER",
    "Nombra 1 cosa buena de ti"
)

val stretchSteps = listOf(
    "Levanta los brazos hacia el techo y estira todo tu cuerpo",
    "Gira suavemente los hombros hacia atrás, despacio",
    "Estira las piernas una por una, sin forzar"
)

data class InlineExerciseState(
    val toolId: Int,
    val steps: List<String>,
    val currentStepIndex: Int = 0,
    val pauseSecondsRemaining: Int? = null
) {
    val isLastStep: Boolean get() = currentStepIndex >= steps.lastIndex
}

data class ToolboxUiState(
    val tools: List<CopingTool> = emptyList(),
    val activeExercise: InlineExerciseState? = null,
    val justCompletedToolId: Int? = null,
    val newlyEarnedBadgeIds: List<Int> = emptyList()
)

class ToolboxViewModel(
    private val toolRepository: ToolRepository,
    private val badgeRepository: BadgeRepository,
    private val relatedEmotionEntryId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolboxUiState())
    val uiState: StateFlow<ToolboxUiState> = _uiState.asStateFlow()

    private var pauseJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            toolRepository.observeTools().collect { tools ->
                _uiState.value = _uiState.value.copy(tools = tools)
            }
        }
    }

    fun startGrounding() {
        _uiState.value = _uiState.value.copy(
            activeExercise = InlineExerciseState(SeedData.ToolIds.GROUNDING_54321, groundingSteps)
        )
    }

    fun startStretch() {
        _uiState.value = _uiState.value.copy(
            activeExercise = InlineExerciseState(SeedData.ToolIds.STRETCH, stretchSteps)
        )
    }

    fun startPause(totalSeconds: Int = 20) {
        pauseJob?.cancel()
        _uiState.value = _uiState.value.copy(
            activeExercise = InlineExerciseState(
                SeedData.ToolIds.PAUSE,
                listOf("Quédate en silencio y respira mientras cuentas"),
                pauseSecondsRemaining = totalSeconds
            )
        )
        pauseJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--
                val current = _uiState.value.activeExercise
                if (current?.toolId == SeedData.ToolIds.PAUSE) {
                    _uiState.value = _uiState.value.copy(activeExercise = current.copy(pauseSecondsRemaining = remaining))
                }
            }
            if (isActive) completeExercise(startedSeconds = totalSeconds)
        }
    }

    fun nextStep() {
        val current = _uiState.value.activeExercise ?: return
        if (current.isLastStep) {
            completeExercise()
        } else {
            _uiState.value = _uiState.value.copy(
                activeExercise = current.copy(currentStepIndex = current.currentStepIndex + 1)
            )
        }
    }

    fun cancelExercise() {
        pauseJob?.cancel()
        _uiState.value = _uiState.value.copy(activeExercise = null)
    }

    private fun completeExercise(startedSeconds: Int = 0) {
        val toolId = _uiState.value.activeExercise?.toolId ?: return
        viewModelScope.launch {
            toolRepository.logSession(
                copingToolId = toolId,
                durationSeconds = startedSeconds,
                relatedEmotionEntryId = relatedEmotionEntryId
            )
            val newBadges = badgeRepository.evaluateAndAward()
            _uiState.value = _uiState.value.copy(
                activeExercise = null,
                justCompletedToolId = toolId,
                newlyEarnedBadgeIds = newBadges
            )
        }
    }

    fun consumeCompletionEvent() {
        _uiState.value = _uiState.value.copy(justCompletedToolId = null, newlyEarnedBadgeIds = emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        pauseJob?.cancel()
    }
}
