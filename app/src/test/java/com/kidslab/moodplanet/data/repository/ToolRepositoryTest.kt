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

/** Pruebas del uso de herramientas (caja de herramientas) y su registro. */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class ToolRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ToolRepository

    @Before
    fun setUp() {
        database = TestDatabaseFactory.create()
        runBlocking { database.seedIfNeeded() }
        repository = ToolRepository(database.copingToolDao(), database.toolSessionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `usar una herramienta queda registrado`() = runTest {
        repository.logSession(SeedData.ToolIds.BREATHING, durationSeconds = 30)

        assertThat(repository.countForTool(SeedData.ToolIds.BREATHING)).isEqualTo(1)
        assertThat(repository.distinctToolsUsed()).isEqualTo(1)
    }

    @Test
    fun `el resumen semanal agrupa por herramienta`() = runTest {
        repository.logSession(SeedData.ToolIds.BREATHING, durationSeconds = 30)
        repository.logSession(SeedData.ToolIds.BREATHING, durationSeconds = 20)
        repository.logSession(SeedData.ToolIds.GROUNDING_54321, durationSeconds = 10)

        val usage = repository.observeUsage(7).first()
        val breathingUsage = usage.first { it.copingToolId == SeedData.ToolIds.BREATHING }
        assertThat(breathingUsage.count).isEqualTo(2)
    }
}
