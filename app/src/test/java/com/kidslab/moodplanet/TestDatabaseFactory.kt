package com.kidslab.moodplanet

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kidslab.moodplanet.data.local.AppDatabase

/** Crea una base de datos Room en memoria para pruebas unitarias (JVM/Robolectric). */
object TestDatabaseFactory {
    fun create(): AppDatabase {
        val context = ApplicationProvider.getApplicationContext<Application>()
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}
