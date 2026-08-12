package com.kidslab.moodplanet

import android.app.Application
import com.kidslab.moodplanet.di.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoodPlanetApplication : Application() {

    lateinit var serviceLocator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        serviceLocator = ServiceLocator(this)
        // Garantiza que la semilla exista aunque el callback de creación de
        // la base de datos no se haya disparado todavía (idempotente).
        CoroutineScope(Dispatchers.IO).launch {
            serviceLocator.ensureSeeded()
        }
    }
}
