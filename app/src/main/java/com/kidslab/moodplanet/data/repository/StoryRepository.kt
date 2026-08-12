package com.kidslab.moodplanet.data.repository

import com.kidslab.moodplanet.data.local.dao.EmotionalStoryDao
import com.kidslab.moodplanet.data.local.dao.StoryAttemptDao
import com.kidslab.moodplanet.data.local.dao.StoryWithOptions
import com.kidslab.moodplanet.data.local.entity.EmotionalStory
import com.kidslab.moodplanet.data.local.entity.StoryAttempt

class StoryRepository(
    private val emotionalStoryDao: EmotionalStoryDao,
    private val storyAttemptDao: StoryAttemptDao
) {
    suspend fun getAllStories(): List<EmotionalStory> = emotionalStoryDao.getAllStories()

    suspend fun getStoryWithOptions(storyId: Int): StoryWithOptions? =
        emotionalStoryDao.getStoryWithOptions(storyId)

    suspend fun recordAttempt(
        storyId: Int,
        chosenEmotionOptionId: Long,
        chosenReactionOptionId: Long,
        wasEmotionCorrect: Boolean,
        wasReactionRecommended: Boolean
    ): Long {
        val attempt = StoryAttempt(
            storyId = storyId,
            chosenEmotionOptionId = chosenEmotionOptionId,
            chosenReactionOptionId = chosenReactionOptionId,
            wasEmotionCorrect = wasEmotionCorrect,
            wasReactionRecommended = wasReactionRecommended
        )
        return storyAttemptDao.insert(attempt)
    }

    suspend fun distinctStoriesCompleted(): Int = storyAttemptDao.distinctStoriesCompleted()
}
