package com.kidslab.moodplanet.data.repository

import com.kidslab.moodplanet.data.DateKeys
import com.kidslab.moodplanet.data.local.dao.EmotionDailyCount
import com.kidslab.moodplanet.data.local.dao.EmotionEntryDao
import com.kidslab.moodplanet.data.local.dao.EmotionEntryWithDetails
import com.kidslab.moodplanet.data.local.dao.EmotionTypeDao
import com.kidslab.moodplanet.data.local.dao.TriggerCategoryDao
import com.kidslab.moodplanet.data.local.entity.EmotionEntry
import com.kidslab.moodplanet.data.local.entity.EmotionType
import com.kidslab.moodplanet.data.local.entity.TriggerCategory
import kotlinx.coroutines.flow.Flow

const val NOTE_MAX_LENGTH = 80

class EmotionRepository(
    private val emotionTypeDao: EmotionTypeDao,
    private val triggerCategoryDao: TriggerCategoryDao,
    private val emotionEntryDao: EmotionEntryDao
) {
    fun observeEmotionTypes(): Flow<List<EmotionType>> = emotionTypeDao.observeAll()

    fun observeTriggerCategories(): Flow<List<TriggerCategory>> = triggerCategoryDao.observeAll()

    fun observeRecentEntries(days: Int = 7): Flow<List<EmotionEntryWithDetails>> =
        emotionEntryDao.observeSince(DateKeys.daysAgo((days - 1).toLong()))

    fun observeDailyCounts(days: Int = 7): Flow<List<EmotionDailyCount>> =
        emotionEntryDao.observeDailyCountsSince(DateKeys.daysAgo((days - 1).toLong()))

    fun observeTotalEntryCount(): Flow<Int> = emotionEntryDao.observeTotalCount()

    /**
     * Guarda un nuevo registro emocional. [note] se recorta a
     * [NOTE_MAX_LENGTH] caracteres como defensa adicional (la UI ya limita
     * la entrada).
     */
    suspend fun logEmotion(
        emotionTypeId: Int,
        intensity: Int,
        triggerCategoryId: Int?,
        note: String
    ): Long {
        require(intensity in 1..3) { "La intensidad debe estar entre 1 y 3" }
        val safeNote = note.take(NOTE_MAX_LENGTH)
        val entry = EmotionEntry(
            emotionTypeId = emotionTypeId,
            intensity = intensity,
            triggerCategoryId = triggerCategoryId,
            note = safeNote,
            dateKey = DateKeys.today()
        )
        return emotionEntryDao.insert(entry)
    }

    suspend fun totalEntryCount(): Int = emotionEntryDao.totalCount()

    suspend fun distinctDaysLogged(): Int = emotionEntryDao.distinctDaysLogged()

    /** Racha de días consecutivos con registro, terminando hoy o ayer. */
    suspend fun currentStreakDays(): Int {
        val dateKeys = emotionEntryDao.distinctDateKeysDesc().toSet()
        if (dateKeys.isEmpty()) return 0
        // Si hoy todavía no tiene registro, la racha se sigue contando desde ayer.
        var offset = if (DateKeys.today() in dateKeys) 0L else 1L
        var streak = 0
        while (DateKeys.daysAgo(offset) in dateKeys) {
            streak++
            offset++
        }
        return streak
    }
}
