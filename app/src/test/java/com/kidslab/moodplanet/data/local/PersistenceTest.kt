package com.kidslab.moodplanet.data.local

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.kidslab.moodplanet.data.local.seed.SeedData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifica que los datos persisten en disco entre "sesiones" de la app:
 * se escribe con una instancia de la base de datos, se cierra, y se vuelven
 * a leer con una instancia nueva apuntando al mismo archivo.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PersistenceTest {

    @Test
    fun `los datos persisten tras cerrar y reabrir la base de datos`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val dbName = "persistence_test_${System.nanoTime()}.db"

        val firstInstance = Room.databaseBuilder(context, AppDatabase::class.java, dbName).build()
        runBlocking {
            firstInstance.seedIfNeeded()
            firstInstance.emotionEntryDao().insert(
                com.kidslab.moodplanet.data.local.entity.EmotionEntry(
                    emotionTypeId = SeedData.EmotionIds.CALMA,
                    intensity = 2,
                    dateKey = com.kidslab.moodplanet.data.DateKeys.today()
                )
            )
        }
        firstInstance.close()

        val secondInstance = Room.databaseBuilder(context, AppDatabase::class.java, dbName).build()
        val count = runBlocking { secondInstance.emotionEntryDao().totalCount() }
        secondInstance.close()

        assertThat(count).isEqualTo(1)
        context.deleteDatabase(dbName)
    }
}
