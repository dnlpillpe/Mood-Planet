package com.kidslab.moodplanet.ui.calmvoice

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.kidslab.moodplanet.TestDatabaseFactory
import com.kidslab.moodplanet.audio.CalmVoiceRecorder
import com.kidslab.moodplanet.data.local.AppDatabase
import com.kidslab.moodplanet.data.local.seed.SeedData
import com.kidslab.moodplanet.data.repository.BadgeRepository
import com.kidslab.moodplanet.data.repository.EmotionRepository
import com.kidslab.moodplanet.data.repository.StoryRepository
import com.kidslab.moodplanet.data.repository.ToolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Prueba clave de privacidad/accesibilidad: si el niño/a niega el permiso de
 * micrófono, el ejercicio "Mi voz tranquila" debe seguir siendo utilizable
 * mediante la alternativa sin micrófono, y la app NO debe bloquearse.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class CalmVoiceViewModelTest {

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
    fun `denegar el permiso de microfono no bloquea el ejercicio`() = runTest(testDispatcher) {
        val viewModel = CalmVoiceViewModel(CalmVoiceRecorder(), toolRepository, badgeRepository, relatedEmotionEntryId = null)

        viewModel.onPermissionResult(granted = false)
        assertThat(viewModel.uiState.value.permissionState).isEqualTo(PermissionState.DENIED)
        assertThat(viewModel.uiState.value.isActive).isFalse()

        // El niño/a puede seguir practicando sin micrófono.
        viewModel.startWithoutMic()
        assertThat(viewModel.uiState.value.isActive).isTrue()
        assertThat(viewModel.uiState.value.isPracticingWithoutMic).isTrue()

        viewModel.finish()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.justFinished).isTrue()
        assertThat(toolRepository.countForTool(SeedData.ToolIds.CALM_VOICE)).isEqualTo(1)
    }
}
