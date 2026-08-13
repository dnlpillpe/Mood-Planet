package com.kidslab.moodplanet.ui.feelings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kidslab.moodplanet.R
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle
import com.kidslab.moodplanet.ui.components.SelectableChip

@Composable
fun HowDoIFeelScreen(
    viewModel: HowDoIFeelViewModel,
    onContinue: (emotionTypeId: Int, intensity: Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ScreenPadding)
    ) {
        SectionTitle(stringResource(R.string.how_do_i_feel_title))
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.emotions) { emotion ->
                SelectableChip(
                    label = emotion.name,
                    emoji = emotion.emoji,
                    selected = state.selectedEmotionId == emotion.id,
                    color = runCatching { Color(android.graphics.Color.parseColor(emotion.colorHex)) }
                        .getOrDefault(MaterialTheme.colorScheme.primary),
                    onClick = { viewModel.selectEmotion(emotion.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (state.selectedEmotionId != null) {
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.intensity_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            val labels = listOf(
                1 to stringResource(R.string.intensity_1),
                2 to stringResource(R.string.intensity_2),
                3 to stringResource(R.string.intensity_3)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                labels.forEach { (level, label) ->
                    SelectableChip(
                        label = label,
                        emoji = null,
                        selected = state.selectedIntensity == level,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { viewModel.selectIntensity(level) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        MoodPrimaryButton(
            text = stringResource(R.string.continue_button),
            enabled = state.canContinue,
            onClick = {
                val emotionId = state.selectedEmotionId
                val intensity = state.selectedIntensity
                if (emotionId != null && intensity != null) {
                    onContinue(emotionId, intensity)
                }
            }
        )
    }
}
