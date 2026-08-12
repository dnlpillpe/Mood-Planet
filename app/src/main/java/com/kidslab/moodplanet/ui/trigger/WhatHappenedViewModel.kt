package com.kidslab.moodplanet.ui.trigger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.entity.TriggerCategory
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.EmotionRepository
import com.kidslab.moodplanet.data.repository.NOTE_MAX_LENGTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WhatHappenedUiState(
    val triggers: List<TriggerCategory> = emptyList(),
    val selectedTriggerId: Int? = null,
    val note: String = "",
    val savedEntryId: Long? = null,
    val newlyEarnedBadgeIds: List<Int> = emptyList()
)

class WhatHappenedViewModel(
    private val emotionRepository: EmotionRepository,
    private val badgeRepository: BadgeRepository,
    private val emotionTypeId: Int,
    private val intensity: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(WhatHappenedUiState())
    val uiState: StateFlow<WhatHappenedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            emotionRepository.observeTriggerCategories().collect { triggers ->
                _uiState.value = _uiState.value.copy(triggers = triggers)
            }
        }
    }

    fun selectTrigger(triggerId: Int?) {
        _uiState.value = _uiState.value.copy(selectedTriggerId = triggerId)
    }

    fun updateNote(text: String) {
        _uiState.value = _uiState.value.copy(note = text.take(NOTE_MAX_LENGTH))
    }

    /** Guarda el registro (el disparador y la nota son opcionales) y evalúa insignias. */
    fun saveEntry() {
        viewModelScope.launch {
            val state = _uiState.value
            val id = emotionRepository.logEmotion(
                emotionTypeId = emotionTypeId,
                intensity = intensity,
                triggerCategoryId = state.selectedTriggerId,
                note = state.note
            )
            val newBadges = badgeRepository.evaluateAndAward()
            _uiState.value = state.copy(savedEntryId = id, newlyEarnedBadgeIds = newBadges)
        }
    }
}
