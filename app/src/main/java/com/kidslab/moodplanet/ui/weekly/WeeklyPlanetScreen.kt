package com.kidslab.moodplanet.ui.weekly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kidslab.moodplanet.R
import com.kidslab.moodplanet.ui.components.EducationalDisclaimer
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle

@Composable
fun WeeklyPlanetScreen(viewModel: WeeklyPlanetViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(ScreenPadding)) {
        SectionTitle(stringResource(R.string.weekly_title))
        Text(
            stringResource(R.string.weekly_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        EducationalDisclaimer(text = stringResource(R.string.weekly_disclaimer))
        Spacer(Modifier.height(16.dp))

        if (!state.hasData) {
            Text(stringResource(R.string.weekly_no_data), style = MaterialTheme.typography.bodyLarge)
            return@Column
        }

        Text("Emociones de la semana", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        val byEmotion = state.dailyCounts.groupBy { it.emotionName to it.emotionEmoji }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            byEmotion.entries.take(4).forEach { (key, values) ->
                val (name, emoji) = key
                val total = values.sumOf { it.count }
                Card(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(emoji, style = MaterialTheme.typography.headlineMedium)
                        Text(name, style = MaterialTheme.typography.labelLarge)
                        Text("$total", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Herramientas utilizadas", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        state.toolUsage.forEach { usage ->
            Text("• ${usage.name}: ${usage.count} veces", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.height(20.dp))
        Text("Registros recientes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.recentEntries) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(entry.emotionEmoji, style = MaterialTheme.typography.titleLarge)
                        Column {
                            Text("${entry.emotionName} · ${entry.entry.dateKey}", style = MaterialTheme.typography.bodyMedium)
                            if (entry.triggerName != null) {
                                Text(entry.triggerName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
