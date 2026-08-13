package com.kidslab.moodplanet.data.repository

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.kidslab.moodplanet.TestDatabaseFactory
import com.kidslab.moodplanet.data.local.AppDatabase
import com.kidslab.moodplanet.data.local.seed.SeedData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Pruebas de: registro de emoción, intensidad válida, y persistencia en Room.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class EmotionRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: EmotionRepository

    @Before
    fun setUp() {
        database = TestDatabaseFactory.create()
        runBlocking { database.seedIfNeeded() }
        repository = EmotionRepository(
            emotionTypeDao = database.emotionTypeDao(),
            triggerCategoryDao = database.triggerCategoryDao(),
            emotionEntryDao = database.emotionEntryDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `registrar una emocion la persiste correctamente`() = runTest {
        val id = repository.logEmotion(
            emotionTypeId = SeedData.EmotionIds.ALEGRIA,
            intensity = 2,
            triggerCategoryId = SeedData.TriggerIds.ESCUELA,
            note = "Un examen salió bien"
        )

        assertThat(id).isGreaterThan(0L)
        assertThat(repository.totalEntryCount()).isEqualTo(1)
    }

    @Test
    fun `la intensidad debe estar entre 1 y 3`() = runTest {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.logEmotion(SeedData.EmotionIds.ENOJO, intensity = 5, triggerCategoryId = null, note = "")
            }
        }
    }

    @Test
    fun `la nota se recorta a 80 caracteres`() = runTest {
        val longNote = "a".repeat(200)
        repository.logEmotion(SeedData.EmotionIds.TRISTEZA, intensity = 1, triggerCategoryId = null, note = longNote)

        val recent = repository.observeRecentEntries(7)
        // La primera emisión del Flow contiene el registro recién guardado.
        val entries = recent.first()
        assertThat(entries).hasSize(1)
        assertThat(entries.first().entry.note.length).isEqualTo(NOTE_MAX_LENGTH)
    }

    @Test
    fun `los registros sobreviven a una nueva instancia de repositorio (persistencia)`() = runTest {
        repository.logEmotion(SeedData.EmotionIds.CALMA, intensity = 1, triggerCategoryId = null, note = "")

        val secondRepository = EmotionRepository(
            emotionTypeDao = database.emotionTypeDao(),
            triggerCategoryDao = database.triggerCategoryDao(),
            emotionEntryDao = database.emotionEntryDao()
        )
        assertThat(secondRepository.totalEntryCount()).isEqualTo(1)
    }
}
