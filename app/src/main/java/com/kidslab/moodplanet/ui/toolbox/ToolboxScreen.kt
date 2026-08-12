package com.kidslab.moodplanet.ui.toolbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.local.seed.SeedData
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle

@Composable
fun ToolboxScreen(
    viewModel: ToolboxViewModel,
    onOpenBreathing: () -> Unit,
    onOpenCalmVoice: () -> Unit,
    onOpenTalkToAdult: () -> Unit,
    onToolCompleted: (toolId: Int, newBadgeIds: List<Int>) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.justCompletedToolId) {
        val toolId = state.justCompletedToolId
        if (toolId != null) {
            onToolCompleted(toolId, state.newlyEarnedBadgeIds)
            viewModel.consumeCompletionEvent()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(ScreenPadding)) {
        SectionTitle(stringResource(R.string.toolbox_title))
        Text(
            stringResource(R.string.toolbox_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(state.tools) { tool ->
                ToolCard(tool) {
                    when (tool.id) {
                        SeedData.ToolIds.BREATHING -> onOpenBreathing()
                        SeedData.ToolIds.GROUNDING_54321 -> viewModel.startGrounding()
                        SeedData.ToolIds.STRETCH -> viewModel.startStretch()
                        SeedData.ToolIds.PAUSE -> viewModel.startPause()
                        SeedData.ToolIds.CALM_VOICE -> onOpenCalmVoice()
                        SeedData.ToolIds.TALK_ADULT -> onOpenTalkToAdult()
                    }
                }
            }
        }
    }

    val exercise = state.activeExercise
    if (exercise != null) {
        AlertDialog(
            onDismissRequest = viewModel::cancelExercise,
            title = { Text(exercise.steps.getOrElse(exercise.currentStepIndex) { "" }) },
            text = {
                if (exercise.pauseSecondsRemaining != null) {
                    Text("${exercise.pauseSecondsRemaining} s", style = MaterialTheme.typography.headlineMedium)
                }
            },
            confirmButton = {
                if (exercise.pauseSecondsRemaining == null) {
                    TextButton(onClick = viewModel::nextStep) {
                        Text(if (exercise.isLastStep) "Terminar" else "Siguiente")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelExercise) { Text(stringResource(R.string.close)) }
            }
        )
    }
}

@Composable
private fun ToolCard(tool: CopingTool, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(tool.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                tool.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
