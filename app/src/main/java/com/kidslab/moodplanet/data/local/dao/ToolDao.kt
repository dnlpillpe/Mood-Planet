package com.kidslab.moodplanet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.local.entity.ToolSession
import kotlinx.coroutines.flow.Flow

data class ToolUsageCount(
    val copingToolId: Int,
    val name: String,
    val iconName: String,
    val count: Int
)

@Dao
interface CopingToolDao {
    @Query("SELECT * FROM coping_tool ORDER BY orderIndex ASC")
    fun observeAll(): Flow<List<CopingTool>>

    @Query("SELECT * FROM coping_tool ORDER BY orderIndex ASC")
    suspend fun getAll(): List<CopingTool>

    @Query("SELECT COUNT(*) FROM coping_tool")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<CopingTool>)
}

@Dao
interface ToolSessionDao {

    @Insert
    suspend fun insert(session: ToolSession): Long

    @Query(
        """
        SELECT ts.copingToolId AS copingToolId, ct.name AS name, ct.iconName AS iconName, COUNT(*) AS count
        FROM tool_session ts
        INNER JOIN coping_tool ct ON ct.id = ts.copingToolId
        WHERE ts.dateKey >= :fromDateKey
        GROUP BY ts.copingToolId
        ORDER BY count DESC
        """
    )
    fun observeUsageSince(fromDateKey: String): Flow<List<ToolUsageCount>>

    @Query("SELECT COUNT(*) FROM tool_session WHERE copingToolId = :copingToolId")
    suspend fun countForTool(copingToolId: Int): Int

    @Query("SELECT COUNT(*) FROM tool_session")
    suspend fun totalCount(): Int

    @Query("SELECT COUNT(DISTINCT copingToolId) FROM tool_session")
    suspend fun distinctToolsUsed(): Int
}
