package com.kidslab.moodplanet.data.repository

import com.kidslab.moodplanet.data.DateKeys
import com.kidslab.moodplanet.data.local.dao.CopingToolDao
import com.kidslab.moodplanet.data.local.dao.ToolSessionDao
import com.kidslab.moodplanet.data.local.dao.ToolUsageCount
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.local.entity.ToolSession
import kotlinx.coroutines.flow.Flow

class ToolRepository(
    private val copingToolDao: CopingToolDao,
    private val toolSessionDao: ToolSessionDao
) {
    fun observeTools(): Flow<List<CopingTool>> = copingToolDao.observeAll()

    suspend fun getTools(): List<CopingTool> = copingToolDao.getAll()

    fun observeUsage(days: Int = 7): Flow<List<ToolUsageCount>> =
        toolSessionDao.observeUsageSince(DateKeys.daysAgo((days - 1).toLong()))

    suspend fun logSession(
        copingToolId: Int,
        durationSeconds: Int,
        relatedEmotionEntryId: Long? = null
    ): Long {
        val session = ToolSession(
            copingToolId = copingToolId,
            durationSeconds = durationSeconds,
            dateKey = DateKeys.today(),
            relatedEmotionEntryId = relatedEmotionEntryId
        )
        return toolSessionDao.insert(session)
    }

    suspend fun countForTool(copingToolId: Int): Int = toolSessionDao.countForTool(copingToolId)

    suspend fun distinctToolsUsed(): Int = toolSessionDao.distinctToolsUsed()
}
