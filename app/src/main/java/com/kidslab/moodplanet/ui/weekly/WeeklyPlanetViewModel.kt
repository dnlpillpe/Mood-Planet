package com.kidslab.moodplanet.ui.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.dao.EmotionDailyCount
import com.kidslab.moodplanet.data.local.dao.EmotionEntryWithDetails
import com.kidslab.moodplanet.data.local.dao.ToolUsageCount
import com.kidslab.moodplanet.data.repository.EmotionRepository
import com.kidslab.moodplanet.data.repository.ToolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class WeeklyPlanetUiState(
    val dailyCounts: List<EmotionDailyCount> = emptyList(),
    val toolUsage: List<ToolUsageCount> = emptyList(),
    val recentEntries: List<EmotionEntryWithDetails> = emptyList()
) {
    val hasData: Boolean get() = recentEntries.isNotEmpty()
}

class WeeklyPlanetViewModel(
    emotionRepository: EmotionRepository,
    toolRepository: ToolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyPlanetUiState())
    val uiState: StateFlow<WeeklyPlanetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                emotionRepository.observeDailyCounts(7),
                toolRepository.observeUsage(7),
                emotionRepository.observeRecentEntries(7)
            ) { daily, tools, entries ->
                WeeklyPlanetUiState(daily, tools, entries)
            }.collect { _uiState.value = it }
        }
    }
}
