package com.kidslab.moodplanet

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kidslab.moodplanet.data.local.AppDatabase
import java.util.concurrent.Executor

/** Crea una base de datos Room en memoria para pruebas unitarias (JVM/Robolectric). */
object TestDatabaseFactory {

    private val directExecutor = Executor { command ->
        command.run()
    }

    fun create(): AppDatabase {
        val context = ApplicationProvider.getApplicationContext<Application>()

        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .setQueryExecutor(directExecutor)
            .setTransactionExecutor(directExecutor)
            .build()
    }
}