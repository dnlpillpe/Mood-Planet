package com.kidslab.moodplanet.ui.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.entity.EmotionalStory
import com.kidslab.moodplanet.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoriesListViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableStateFlow<List<EmotionalStory>>(emptyList())
    val stories: StateFlow<List<EmotionalStory>> = _stories.asStateFlow()

    init {
        viewModelScope.launch {
            _stories.value = storyRepository.getAllStories()
        }
    }
}
