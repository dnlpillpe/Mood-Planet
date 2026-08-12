package com.kidslab.moodplanet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kidslab.moodplanet.data.local.entity.EmotionalStory
import com.kidslab.moodplanet.data.local.entity.StoryAttempt
import com.kidslab.moodplanet.data.local.entity.StoryOption

data class StoryWithOptions(
    val story: EmotionalStory,
    val emotionOptions: List<StoryOption>,
    val reactionOptions: List<StoryOption>
)

@Dao
interface EmotionalStoryDao {

    @Query("SELECT * FROM emotional_story ORDER BY orderIndex ASC")
    suspend fun getAllStories(): List<EmotionalStory>

    @Query("SELECT COUNT(*) FROM emotional_story")
    suspend fun countStories(): Int

    @Query("SELECT * FROM story_option WHERE storyId = :storyId AND stepType = :stepType ORDER BY orderIndex ASC")
    suspend fun getOptions(storyId: Int, stepType: String): List<StoryOption>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(items: List<EmotionalStory>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOptions(items: List<StoryOption>)

    @Transaction
    suspend fun getStoryWithOptions(storyId: Int): StoryWithOptions? {
        val story = getAllStories().firstOrNull { it.id == storyId } ?: return null
        return StoryWithOptions(
            story = story,
            emotionOptions = getOptions(storyId, "EMOTION"),
            reactionOptions = getOptions(storyId, "REACTION")
        )
    }
}

@Dao
interface StoryAttemptDao {

    @Insert
    suspend fun insert(attempt: StoryAttempt): Long

    @Query("SELECT * FROM story_attempt WHERE storyId = :storyId ORDER BY completedAt DESC LIMIT 1")
    suspend fun latestForStory(storyId: Int): StoryAttempt?

    @Query("SELECT COUNT(DISTINCT storyId) FROM story_attempt")
    suspend fun distinctStoriesCompleted(): Int

    @Query("SELECT COUNT(*) FROM story_attempt")
    suspend fun totalCount(): Int
}
