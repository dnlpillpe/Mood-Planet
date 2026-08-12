package com.kidslab.moodplanet.ui.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.dao.StoryWithOptions
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StoryStep { EMOTION, REACTION, FEEDBACK }

data class StoryDetailUiState(
    val storyWithOptions: StoryWithOptions? = null,
    val step: StoryStep = StoryStep.EMOTION,
    val chosenEmotionOptionId: Long? = null,
    val chosenReactionOptionId: Long? = null,
    val wasEmotionCorrect: Boolean = false,
    val wasReactionRecommended: Boolean = false,
    val newlyEarnedBadgeIds: List<Int> = emptyList()
)

class StoryDetailViewModel(
    private val storyRepository: StoryRepository,
    private val badgeRepository: BadgeRepository,
    private val storyId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoryDetailUiState())
    val uiState: StateFlow<StoryDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                storyWithOptions = storyRepository.getStoryWithOptions(storyId)
            )
        }
    }

    fun chooseEmotion(optionId: Long) {
        val options = _uiState.value.storyWithOptions?.emotionOptions.orEmpty()
        val chosen = options.firstOrNull { it.id == optionId }
        _uiState.value = _uiState.value.copy(
            chosenEmotionOptionId = optionId,
            wasEmotionCorrect = chosen?.isRecommended == true,
            step = StoryStep.REACTION
        )
    }

    fun chooseReaction(optionId: Long) {
        val options = _uiState.value.storyWithOptions?.reactionOptions.orEmpty()
        val chosen = options.firstOrNull { it.id == optionId }
        val state = _uiState.value
        _uiState.value = state.copy(
            chosenReactionOptionId = optionId,
            wasReactionRecommended = chosen?.isRecommended == true,
            step = StoryStep.FEEDBACK
        )
        viewModelScope.launch {
            val emotionId = _uiState.value.chosenEmotionOptionId ?: return@launch
            storyRepository.recordAttempt(
                storyId = storyId,
                chosenEmotionOptionId = emotionId,
                chosenReactionOptionId = optionId,
                wasEmotionCorrect = _uiState.value.wasEmotionCorrect,
                wasReactionRecommended = _uiState.value.wasReactionRecommended
            )
            val newBadges = badgeRepository.evaluateAndAward()
            _uiState.value = _uiState.value.copy(newlyEarnedBadgeIds = newBadges)
        }
    }
}
