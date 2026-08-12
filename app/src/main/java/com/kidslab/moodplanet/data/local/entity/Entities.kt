package com.kidslab.moodplanet.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Perfil local del niño/a que usa la app. Fila única (id fijo = 1) usada
 * para personalizar la experiencia. No incluye datos identificables
 * obligatorios: el nombre es opcional y se guarda solo en el dispositivo.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val childName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val dailyReminderEnabled: Boolean = false,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}

/** Catálogo fijo de 8 emociones que el niño puede reconocer. */
@Entity(tableName = "emotion_type")
data class EmotionType(
    @PrimaryKey val id: Int,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val orderIndex: Int
)

/** Catálogo fijo de 6 categorías de disparador ("¿Qué pasó?"). */
@Entity(tableName = "trigger_category")
data class TriggerCategory(
    @PrimaryKey val id: Int,
    val name: String,
    val emoji: String,
    val orderIndex: Int
)

/**
 * Un registro emocional individual: la emoción elegida, su intensidad
 * (1-3) y, opcionalmente, qué pasó. No representa un diagnóstico.
 */
@Entity(
    tableName = "emotion_entry",
    foreignKeys = [
        ForeignKey(
            entity = EmotionType::class,
            parentColumns = ["id"],
            childColumns = ["emotionTypeId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TriggerCategory::class,
            parentColumns = ["id"],
            childColumns = ["triggerCategoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("emotionTypeId"), Index("triggerCategoryId"), Index("dateKey")]
)
data class EmotionEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emotionTypeId: Int,
    @ColumnInfo(defaultValue = "1") val intensity: Int,
    val triggerCategoryId: Int? = null,
    @ColumnInfo(defaultValue = "") val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    /** Clave "yyyy-MM-dd" en huso horario local, para agrupar por día. */
    val dateKey: String
)

/** Catálogo fijo de 6 herramientas de la caja de herramientas. */
@Entity(tableName = "coping_tool")
data class CopingTool(
    @PrimaryKey val id: Int,
    val key: String,
    val name: String,
    val description: String,
    val iconName: String,
    val orderIndex: Int
)

/** Registro de que se usó una herramienta (para el resumen semanal). */
@Entity(
    tableName = "tool_session",
    foreignKeys = [
        ForeignKey(
            entity = CopingTool::class,
            parentColumns = ["id"],
            childColumns = ["copingToolId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("copingToolId"), Index("dateKey")]
)
data class ToolSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val copingToolId: Int,
    val startedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val dateKey: String,
    val relatedEmotionEntryId: Long? = null
)

/** Una de las 15 historias emocionales interactivas. */
@Entity(
    tableName = "emotional_story",
    foreignKeys = [
        ForeignKey(
            entity = EmotionType::class,
            parentColumns = ["id"],
            childColumns = ["targetEmotionTypeId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("targetEmotionTypeId")]
)
data class EmotionalStory(
    @PrimaryKey val id: Int,
    val title: String,
    val scenarioText: String,
    /** Emoción que mejor describe al personaje de la historia. */
    val targetEmotionTypeId: Int,
    val orderIndex: Int
)

/**
 * Una opción dentro de una historia. `stepType` es "EMOTION" (identificar
 * la emoción) o "REACTION" (elegir cómo reaccionar). Cada historia tiene
 * 3 opciones de emoción y 3 opciones de reacción.
 */
@Entity(
    tableName = "story_option",
    foreignKeys = [
        ForeignKey(
            entity = EmotionalStory::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("storyId")]
)
data class StoryOption(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storyId: Int,
    val stepType: String,
    val text: String,
    /** Correcta (para EMOTION) o recomendada/saludable (para REACTION). */
    val isRecommended: Boolean,
    val orderIndex: Int
)

/** Intento de un niño/a al resolver una historia (para no repetir feedback). */
@Entity(
    tableName = "story_attempt",
    foreignKeys = [
        ForeignKey(
            entity = EmotionalStory::class,
            parentColumns = ["id"],
            childColumns = ["storyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("storyId")]
)
data class StoryAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storyId: Int,
    val chosenEmotionOptionId: Long,
    val chosenReactionOptionId: Long,
    val wasEmotionCorrect: Boolean,
    val wasReactionRecommended: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

/** Catálogo fijo de 7 insignias/logros. */
@Entity(tableName = "badge")
data class Badge(
    @PrimaryKey val id: Int,
    val key: String,
    val name: String,
    val description: String,
    val iconName: String,
    val orderIndex: Int
)

/** Marca que una insignia fue obtenida por el usuario. */
@Entity(
    tableName = "user_badge",
    foreignKeys = [
        ForeignKey(
            entity = Badge::class,
            parentColumns = ["id"],
            childColumns = ["badgeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("badgeId", unique = true)]
)
data class UserBadge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val badgeId: Int,
    val earnedAt: Long = System.currentTimeMillis()
)
