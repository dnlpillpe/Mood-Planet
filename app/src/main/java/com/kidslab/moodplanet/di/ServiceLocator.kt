package com.kidslab.moodplanet.di

import android.content.Context
import com.kidslab.moodplanet.data.local.AppDatabase
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.EmotionRepository
import com.kidslab.moodplanet.data.repository.StoryRepository
import com.kidslab.moodplanet.data.repository.ToolRepository
import com.kidslab.moodplanet.data.repository.UserRepository

/**
 * Localizador de servicios simple: crea y comparte una única instancia de
 * la base de datos y de cada repositorio. Se eligió sobre un framework de
 * inyección de dependencias (p. ej. Hilt) para mantener el proyecto ligero
 * y fácil de compilar sin procesadores de anotaciones adicionales.
 */
class ServiceLocator(context: Context) {

    private val appContext = context.applicationContext
    private val database: AppDatabase by lazy { AppDatabase.getInstance(appContext) }

    val emotionRepository: EmotionRepository by lazy {
        EmotionRepository(
            emotionTypeDao = database.emotionTypeDao(),
            triggerCategoryDao = database.triggerCategoryDao(),
            emotionEntryDao = database.emotionEntryDao()
        )
    }

    val toolRepository: ToolRepository by lazy {
        ToolRepository(
            copingToolDao = database.copingToolDao(),
            toolSessionDao = database.toolSessionDao()
        )
    }

    val storyRepository: StoryRepository by lazy {
        StoryRepository(
            emotionalStoryDao = database.emotionalStoryDao(),
            storyAttemptDao = database.storyAttemptDao()
        )
    }

    val badgeRepository: BadgeRepository by lazy {
        BadgeRepository(
            badgeDao = database.badgeDao(),
            userBadgeDao = database.userBadgeDao(),
            emotionRepository = emotionRepository,
            toolRepository = toolRepository,
            storyRepository = storyRepository
        )
    }

    val userRepository: UserRepository by lazy {
        UserRepository(userProfileDao = database.userProfileDao())
    }

    suspend fun ensureSeeded() {
        database.seedIfNeeded()
    }
}
