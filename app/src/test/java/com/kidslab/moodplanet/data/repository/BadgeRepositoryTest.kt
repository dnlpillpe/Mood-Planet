package com.kidslab.moodplanet.data.repository

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.kidslab.moodplanet.TestDatabaseFactory
import com.kidslab.moodplanet.data.local.AppDatabase
import com.kidslab.moodplanet.data.local.seed.SeedData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Pruebas de las 7 insignias/logros. */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class BadgeRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var emotionRepository: EmotionRepository
    private lateinit var toolRepository: ToolRepository
    private lateinit var storyRepository: StoryRepository
    private lateinit var badgeRepository: BadgeRepository

    @Before
    fun setUp() {
        database = TestDatabaseFactory.create()
        runBlocking { database.seedIfNeeded() }
        emotionRepository = EmotionRepository(
            database.emotionTypeDao(), database.triggerCategoryDao(), database.emotionEntryDao()
        )
        toolRepository = ToolRepository(database.copingToolDao(), database.toolSessionDao())
        storyRepository = StoryRepository(database.emotionalStoryDao(), database.storyAttemptDao())
        badgeRepository = BadgeRepository(
            database.badgeDao(), database.userBadgeDao(), emotionRepository, toolRepository, storyRepository
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `hay 7 insignias sembradas y ninguna otorgada al inicio`() = runTest {
        assertThat(database.badgeDao().count()).isEqualTo(7)
        assertThat(database.userBadgeDao().earnedBadgeIds()).isEmpty()
    }

    @Test
    fun `la insignia Primer paso se otorga tras el primer registro`() = runTest {
        emotionRepository.logEmotion(SeedData.EmotionIds.ALEGRIA, 1, null, "")

        val newBadges = badgeRepository.evaluateAndAward()

        assertThat(newBadges).contains(SeedData.BadgeIds.FIRST_ENTRY)
    }

    @Test
    fun `la insignia Maestro de la respiracion requiere 5 usos`() = runTest {
        repeat(4) { toolRepository.logSession(SeedData.ToolIds.BREATHING, 60) }
        assertThat(badgeRepository.evaluateAndAward()).doesNotContain(SeedData.BadgeIds.BREATHING_MASTER)

        toolRepository.logSession(SeedData.ToolIds.BREATHING, 60)
        assertThat(badgeRepository.evaluateAndAward()).contains(SeedData.BadgeIds.BREATHING_MASTER)
    }

    @Test
    fun `una insignia ya otorgada no se vuelve a otorgar`() = runTest {
        emotionRepository.logEmotion(SeedData.EmotionIds.ALEGRIA, 1, null, "")
        val firstAward = badgeRepository.evaluateAndAward()
        assertThat(firstAward).contains(SeedData.BadgeIds.FIRST_ENTRY)

        val secondAward = badgeRepository.evaluateAndAward()
        assertThat(secondAward).doesNotContain(SeedData.BadgeIds.FIRST_ENTRY)
    }
}
