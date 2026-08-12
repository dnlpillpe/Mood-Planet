package com.kidslab.moodplanet.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.dao.BadgeWithStatus
import com.kidslab.moodplanet.data.local.entity.UserProfile
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AchievementsSettingsUiState(
    val badges: List<BadgeWithStatus> = emptyList(),
    val profile: UserProfile = UserProfile()
)

class AchievementsSettingsViewModel(
    private val badgeRepository: BadgeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsSettingsUiState())
    val uiState: StateFlow<AchievementsSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                badgeRepository.observeBadgesWithStatus(),
                userRepository.observeProfile()
            ) { badges, profile ->
                AchievementsSettingsUiState(badges, profile ?: UserProfile())
            }.collect { _uiState.value = it }
        }
    }

    fun updateChildName(name: String) {
        viewModelScope.launch { userRepository.updateChildName(name) }
    }

    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch { userRepository.updateReminder(enabled, hour, minute) }
    }
}
