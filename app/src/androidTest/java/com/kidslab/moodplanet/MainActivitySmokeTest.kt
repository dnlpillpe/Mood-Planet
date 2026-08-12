package com.kidslab.moodplanet

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba instrumentada de humo: la app arranca y muestra la pantalla de
 * bienvenida sin errores, con la base de datos real (Room sobre SQLite).
 */
@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun welcomeScreenIsDisplayedOnLaunch() {
        composeTestRule.onNodeWithText("¡Hola! Soy Nova").assertExists()
    }
}
