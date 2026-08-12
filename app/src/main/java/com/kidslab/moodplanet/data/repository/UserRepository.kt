package com.kidslab.moodplanet.data.repository

import com.kidslab.moodplanet.data.local.dao.UserProfileDao
import com.kidslab.moodplanet.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userProfileDao: UserProfileDao) {

    fun observeProfile(): Flow<UserProfile?> = userProfileDao.observe()

    suspend fun updateChildName(name: String) {
        val current = userProfileDao.get() ?: UserProfile()
        userProfileDao.update(current.copy(childName = name.take(40)))
    }

    suspend fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        val current = userProfileDao.get() ?: UserProfile()
        userProfileDao.update(
            current.copy(dailyReminderEnabled = enabled, reminderHour = hour, reminderMinute = minute)
        )
    }
}
