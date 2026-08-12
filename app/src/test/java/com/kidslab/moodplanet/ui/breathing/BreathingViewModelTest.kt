package com.kidslab.moodplanet.ui.breathing

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.kidslab.moodplanet.TestDatabaseFactory
import com.kidslab.moodplanet.data.local.AppDatabase
import com.kidslab.moodplanet.data.local.seed.SeedData
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.EmotionRepository
import com.kidslab.moodplanet.data.repository.StoryRepository
import com.kidslab.moodplanet.data.repository.ToolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Prueba del ejercicio de respiración: 3 rondas con temporizador 4-2-4. */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class BreathingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: AppDatabase
    private lateinit var toolRepository: ToolRepository
    private lateinit var badgeRepository: BadgeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        database = TestDatabaseFactory.create()
        runBlocking { database.seedIfNeeded() }
        toolRepository = ToolRepository(database.copingToolDao(), database.toolSessionDao())
        val emotionRepository = EmotionRepository(
            database.emotionTypeDao(), database.triggerCategoryDao(), database.emotionEntryDao()
        )
        val storyRepository = StoryRepository(database.emotionalStoryDao(), database.storyAttemptDao())
        badgeRepository = BadgeRepository(
            database.badgeDao(), database.userBadgeDao(), emotionRepository, toolRepository, storyRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun `el ejercicio de respiracion completa 3 rondas y registra la sesion`() = runTest(testDispatcher) {
        val viewModel = BreathingViewModel(toolRepository, badgeRepository, relatedEmotionEntryId = null)

        viewModel.start()
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertThat(finalState.isFinished).isTrue()
        assertThat(toolRepository.countForTool(SeedData.ToolIds.BREATHING)).isEqualTo(1)
    }
}
