package com.kidslab.moodplanet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kidslab.moodplanet.navigation.MoodPlanetNavHost
import com.kidslab.moodplanet.ui.theme.MoodPlanetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val serviceLocator = (application as MoodPlanetApplication).serviceLocator

        setContent {
            MoodPlanetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MoodPlanetNavHost(serviceLocator = serviceLocator)
                }
            }
        }
    }
}
