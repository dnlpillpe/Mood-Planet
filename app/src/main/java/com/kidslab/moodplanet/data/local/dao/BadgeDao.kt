package com.kidslab.moodplanet.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.moodplanet.data.local.entity.Badge
import com.kidslab.moodplanet.data.local.entity.UserBadge
import kotlinx.coroutines.flow.Flow

data class BadgeWithStatus(
    @Embedded val badge: Badge,
    val earnedAt: Long?
)

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badge ORDER BY orderIndex ASC")
    fun observeAll(): Flow<List<Badge>>

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Badge>)

    @Query(
        """
        SELECT b.*, ub.earnedAt AS earnedAt
        FROM badge b
        LEFT JOIN user_badge ub ON ub.badgeId = b.id
        ORDER BY b.orderIndex ASC
        """
    )
    fun observeAllWithStatus(): Flow<List<BadgeWithStatus>>
}

@Dao
interface UserBadgeDao {

    @Query("SELECT badgeId FROM user_badge")
    suspend fun earnedBadgeIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userBadge: UserBadge)
}
