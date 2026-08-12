package com.kidslab.moodplanet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kidslab.moodplanet.data.local.dao.BadgeDao
import com.kidslab.moodplanet.data.local.dao.CopingToolDao
import com.kidslab.moodplanet.data.local.dao.EmotionEntryDao
import com.kidslab.moodplanet.data.local.dao.EmotionTypeDao
import com.kidslab.moodplanet.data.local.dao.EmotionalStoryDao
import com.kidslab.moodplanet.data.local.dao.StoryAttemptDao
import com.kidslab.moodplanet.data.local.dao.ToolSessionDao
import com.kidslab.moodplanet.data.local.dao.TriggerCategoryDao
import com.kidslab.moodplanet.data.local.dao.UserBadgeDao
import com.kidslab.moodplanet.data.local.dao.UserProfileDao
import com.kidslab.moodplanet.data.local.entity.Badge
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.local.entity.EmotionEntry
import com.kidslab.moodplanet.data.local.entity.EmotionType
import com.kidslab.moodplanet.data.local.entity.EmotionalStory
import com.kidslab.moodplanet.data.local.entity.StoryAttempt
import com.kidslab.moodplanet.data.local.entity.StoryOption
import com.kidslab.moodplanet.data.local.entity.ToolSession
import com.kidslab.moodplanet.data.local.entity.TriggerCategory
import com.kidslab.moodplanet.data.local.entity.UserBadge
import com.kidslab.moodplanet.data.local.entity.UserProfile
import com.kidslab.moodplanet.data.local.seed.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos local (Room) de Mood Planet. 100% offline: no hay
 * sincronización remota ni backend. Todos los datos permanecen en el
 * dispositivo del usuario.
 */
@Database(
    entities = [
        UserProfile::class,
        EmotionType::class,
        EmotionEntry::class,
        TriggerCategory::class,
        CopingTool::class,
        ToolSession::class,
        EmotionalStory::class,
        StoryOption::class,
        StoryAttempt::class,
        Badge::class,
        UserBadge::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun emotionTypeDao(): EmotionTypeDao
    abstract fun triggerCategoryDao(): TriggerCategoryDao
    abstract fun emotionEntryDao(): EmotionEntryDao
    abstract fun copingToolDao(): CopingToolDao
    abstract fun toolSessionDao(): ToolSessionDao
    abstract fun emotionalStoryDao(): EmotionalStoryDao
    abstract fun storyAttemptDao(): StoryAttemptDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userBadgeDao(): UserBadgeDao

    companion object {
        private const val DATABASE_NAME = "mood_planet.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: build(context.applicationContext).also { instance = it }
            }
        }

        private fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // La semilla se inserta de forma asíncrona apenas se crea el
                        // archivo de base de datos por primera vez.
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).seedIfNeeded()
                        }
                    }
                })
                .build()
        }
    }

    /** Inserta los datos semilla si todavía no existen (idempotente). */
    suspend fun seedIfNeeded() {
        if (emotionTypeDao().count() == 0) {
            emotionTypeDao().insertAll(SeedData.emotionTypes)
        }
        if (triggerCategoryDao().count() == 0) {
            triggerCategoryDao().insertAll(SeedData.triggerCategories)
        }
        if (copingToolDao().count() == 0) {
            copingToolDao().insertAll(SeedData.copingTools)
        }
        if (badgeDao().count() == 0) {
            badgeDao().insertAll(SeedData.badges)
        }
        if (emotionalStoryDao().countStories() == 0) {
            emotionalStoryDao().insertStories(SeedData.emotionalStories)
            emotionalStoryDao().insertOptions(SeedData.storyOptions)
        }
        if (userProfileDao().get() == null) {
            userProfileDao().insert(UserProfile())
        }
    }
}
