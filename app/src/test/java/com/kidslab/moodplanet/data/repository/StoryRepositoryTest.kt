package com.kidslab.moodplanet.data.repository

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.kidslab.moodplanet.TestDatabaseFactory
import com.kidslab.moodplanet.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Pruebas de las 15 historias emocionales y sus intentos. */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class StoryRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: StoryRepository

    @Before
    fun setUp() {
        database = TestDatabaseFactory.create()
        runBlocking { database.seedIfNeeded() }
        repository = StoryRepository(database.emotionalStoryDao(), database.storyAttemptDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `existen 15 historias sembradas, cada una con 3 opciones de emocion y 3 de reaccion`() = runTest {
        val stories = repository.getAllStories()
        assertThat(stories).hasSize(15)

        stories.forEach { story ->
            val withOptions = repository.getStoryWithOptions(story.id)
            assertThat(withOptions).isNotNull()
            assertThat(withOptions!!.emotionOptions).hasSize(3)
            assertThat(withOptions.reactionOptions).hasSize(3)
            assertThat(withOptions.emotionOptions.count { it.isRecommended }).isEqualTo(1)
        }
    }

    @Test
    fun `completar una historia se registra como intento`() = runTest {
        val storyWithOptions = repository.getStoryWithOptions(1)!!
        val emotionOption = storyWithOptions.emotionOptions.first()
        val reactionOption = storyWithOptions.reactionOptions.first { it.isRecommended }

        repository.recordAttempt(
            storyId = 1,
            chosenEmotionOptionId = emotionOption.id,
            chosenReactionOptionId = reactionOption.id,
            wasEmotionCorrect = emotionOption.isRecommended,
            wasReactionRecommended = true
        )

        assertThat(repository.distinctStoriesCompleted()).isEqualTo(1)
    }
}
