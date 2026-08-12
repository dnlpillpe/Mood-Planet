package com.kidslab.moodplanet.ui.feelings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.entity.EmotionType
import com.kidslab.moodplanet.data.repository.EmotionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HowDoIFeelUiState(
    val emotions: List<EmotionType> = emptyList(),
    val selectedEmotionId: Int? = null,
    val selectedIntensity: Int? = null
) {
    val canContinue: Boolean get() = selectedEmotionId != null && selectedIntensity != null
}

class HowDoIFeelViewModel(private val emotionRepository: EmotionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HowDoIFeelUiState())
    val uiState: StateFlow<HowDoIFeelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            emotionRepository.observeEmotionTypes().collect { emotions ->
                _uiState.value = _uiState.value.copy(emotions = emotions)
            }
        }
    }

    fun selectEmotion(emotionTypeId: Int) {
        _uiState.value = _uiState.value.copy(selectedEmotionId = emotionTypeId)
    }

    fun selectIntensity(intensity: Int) {
        _uiState.value = _uiState.value.copy(selectedIntensity = intensity)
    }
}
