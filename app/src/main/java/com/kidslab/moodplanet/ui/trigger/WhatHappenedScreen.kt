package com.kidslab.moodplanet.ui.trigger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kidslab.moodplanet.R
import com.kidslab.moodplanet.data.repository.NOTE_MAX_LENGTH
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle
import com.kidslab.moodplanet.ui.components.SelectableChip

@Composable
fun WhatHappenedScreen(
    viewModel: WhatHappenedViewModel,
    onSaved: (entryId: Long, newBadgeIds: List<Int>) -> Unit,
    onSkip: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.savedEntryId) {
        val id = state.savedEntryId
        if (id != null) {
            onSaved(id, state.newlyEarnedBadgeIds)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ScreenPadding)
    ) {
        SectionTitle(stringResource(R.string.what_happened_title))
        Text(
            stringResource(R.string.what_happened_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(state.triggers) { trigger ->
                SelectableChip(
                    label = trigger.name,
                    emoji = trigger.emoji,
                    selected = state.selectedTriggerId == trigger.id,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {
                        val newSelection = if (state.selectedTriggerId == trigger.id) null else trigger.id
                        viewModel.selectTrigger(newSelection)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::updateNote,
            label = { Text(stringResource(R.string.note_hint)) },
            supportingText = { Text(stringResource(R.string.note_char_limit, state.note.length, NOTE_MAX_LENGTH)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        MoodPrimaryButton(text = stringResource(R.string.save_button), onClick = viewModel::saveEntry)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.skip_button))
        }
    }
}
