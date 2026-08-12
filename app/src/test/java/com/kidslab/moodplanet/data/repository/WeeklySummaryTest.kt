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

/** Pruebas del resumen semanal (últimos 7 días) de "Mi planeta semanal". */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class WeeklySummaryTest {

    private lateinit var database: AppDatabase
    private lateinit var emotionRepository: EmotionRepository

    @Before
    fun setUp() {
        database = TestDatabaseFactory.create()
        runBlocking { database.seedIfNeeded() }
        emotionRepository = EmotionRepository(
            database.emotionTypeDao(), database.triggerCategoryDao(), database.emotionEntryDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `el resumen semanal incluye los registros de hoy`() = runTest {
        emotionRepository.logEmotion(SeedData.EmotionIds.ALEGRIA, 2, SeedData.TriggerIds.JUEGO, "")
        emotionRepository.logEmotion(SeedData.EmotionIds.ALEGRIA, 3, null, "")
        emotionRepository.logEmotion(SeedData.EmotionIds.TRISTEZA, 1, null, "")

        val dailyCounts = emotionRepository.observeDailyCounts(7).first()
        val totalAlegria = dailyCounts.filter { it.emotionTypeId == SeedData.EmotionIds.ALEGRIA }.sumOf { it.count }

        assertThat(totalAlegria).isEqualTo(2)
        assertThat(dailyCounts.sumOf { it.count }).isEqualTo(3)
    }

    @Test
    fun `sin registros el resumen semanal esta vacio`() = runTest {
        val entries = emotionRepository.observeRecentEntries(7).first()
        assertThat(entries).isEmpty()
    }
}
