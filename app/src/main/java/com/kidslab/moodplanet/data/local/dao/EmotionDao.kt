package com.kidslab.moodplanet.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.moodplanet.data.local.entity.EmotionEntry
import com.kidslab.moodplanet.data.local.entity.EmotionType
import com.kidslab.moodplanet.data.local.entity.TriggerCategory
import kotlinx.coroutines.flow.Flow

data class EmotionEntryWithDetails(
    @Embedded val entry: EmotionEntry,
    val emotionName: String,
    val emotionEmoji: String,
    val emotionColorHex: String,
    val triggerName: String?
)

data class EmotionDailyCount(
    val dateKey: String,
    val emotionTypeId: Int,
    val emotionName: String,
    val emotionEmoji: String,
    val emotionColorHex: String,
    val count: Int
)

@Dao
interface EmotionTypeDao {
    @Query("SELECT * FROM emotion_type ORDER BY orderIndex ASC")
    fun observeAll(): Flow<List<EmotionType>>

    @Query("SELECT * FROM emotion_type ORDER BY orderIndex ASC")
    suspend fun getAll(): List<EmotionType>

    @Query("SELECT COUNT(*) FROM emotion_type")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<EmotionType>)
}

@Dao
interface TriggerCategoryDao {
    @Query("SELECT * FROM trigger_category ORDER BY orderIndex ASC")
    fun observeAll(): Flow<List<TriggerCategory>>

    @Query("SELECT COUNT(*) FROM trigger_category")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<TriggerCategory>)
}

@Dao
interface EmotionEntryDao {

    @Insert
    suspend fun insert(entry: EmotionEntry): Long

    @Query(
        """
        SELECT e.*, t.name AS emotionName, t.emoji AS emotionEmoji, t.colorHex AS emotionColorHex,
               tc.name AS triggerName
        FROM emotion_entry e
        INNER JOIN emotion_type t ON t.id = e.emotionTypeId
        LEFT JOIN trigger_category tc ON tc.id = e.triggerCategoryId
        WHERE e.dateKey >= :fromDateKey
        ORDER BY e.createdAt DESC
        """
    )
    fun observeSince(fromDateKey: String): Flow<List<EmotionEntryWithDetails>>

    @Query(
        """
        SELECT e.dateKey AS dateKey, e.emotionTypeId AS emotionTypeId, t.name AS emotionName,
               t.emoji AS emotionEmoji, t.colorHex AS emotionColorHex, COUNT(*) AS count
        FROM emotion_entry e
        INNER JOIN emotion_type t ON t.id = e.emotionTypeId
        WHERE e.dateKey >= :fromDateKey
        GROUP BY e.dateKey, e.emotionTypeId
        ORDER BY e.dateKey ASC
        """
    )
    fun observeDailyCountsSince(fromDateKey: String): Flow<List<EmotionDailyCount>>

    @Query("SELECT COUNT(*) FROM emotion_entry")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM emotion_entry")
    suspend fun totalCount(): Int

    @Query("SELECT COUNT(DISTINCT dateKey) FROM emotion_entry")
    suspend fun distinctDaysLogged(): Int

    /** Días consecutivos (terminando hoy) con al menos un registro, calculado en Kotlin a partir de las fechas. */
    @Query("SELECT DISTINCT dateKey FROM emotion_entry ORDER BY dateKey DESC")
    suspend fun distinctDateKeysDesc(): List<String>
}
