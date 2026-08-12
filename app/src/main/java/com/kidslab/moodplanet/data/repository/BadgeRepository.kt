package com.kidslab.moodplanet.data.repository

import com.kidslab.moodplanet.data.local.dao.BadgeDao
import com.kidslab.moodplanet.data.local.dao.BadgeWithStatus
import com.kidslab.moodplanet.data.local.dao.UserBadgeDao
import com.kidslab.moodplanet.data.local.entity.UserBadge
import com.kidslab.moodplanet.data.local.seed.SeedData
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de insignias. `evaluateAndAward` revisa las 7 condiciones
 * fijas y otorga las insignias nuevas que ya se cumplieron. Se pensó
 * para llamarse después de cada acción relevante (registrar emoción,
 * usar una herramienta, completar una historia).
 */
class BadgeRepository(
    private val badgeDao: BadgeDao,
    private val userBadgeDao: UserBadgeDao,
    private val emotionRepository: EmotionRepository,
    private val toolRepository: ToolRepository,
    private val storyRepository: StoryRepository
) {
    fun observeBadgesWithStatus(): Flow<List<BadgeWithStatus>> = badgeDao.observeAllWithStatus()

    /** Devuelve la lista de ids de insignias recién otorgadas (para celebrarlas en la UI). */
    suspend fun evaluateAndAward(): List<Int> {
        val alreadyEarned = userBadgeDao.earnedBadgeIds().toMutableSet()
        val newlyEarned = mutableListOf<Int>()

        suspend fun award(badgeId: Int, condition: suspend () -> Boolean) {
            if (badgeId in alreadyEarned) return
            if (condition()) {
                userBadgeDao.insert(UserBadge(badgeId = badgeId))
                alreadyEarned += badgeId
                newlyEarned += badgeId
            }
        }

        award(SeedData.BadgeIds.FIRST_ENTRY) { emotionRepository.totalEntryCount() >= 1 }
        award(SeedData.BadgeIds.EXPLORER) { emotionRepository.totalEntryCount() >= 5 }
        award(SeedData.BadgeIds.CONSISTENT_WEEK) { emotionRepository.distinctDaysLogged() >= 7 }
        award(SeedData.BadgeIds.BREATHING_MASTER) {
            toolRepository.countForTool(SeedData.ToolIds.BREATHING) >= 5
        }
        award(SeedData.BadgeIds.FULL_TOOLBOX) { toolRepository.distinctToolsUsed() >= 6 }
        award(SeedData.BadgeIds.STORYTELLER) { storyRepository.distinctStoriesCompleted() >= 15 }
        award(SeedData.BadgeIds.CALM_VOICE_PRO) {
            toolRepository.countForTool(SeedData.ToolIds.CALM_VOICE) >= 3
        }

        return newlyEarned
    }
}
