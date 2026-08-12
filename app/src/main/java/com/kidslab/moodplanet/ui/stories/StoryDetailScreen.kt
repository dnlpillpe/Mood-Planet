package com.kidslab.moodplanet.ui.stories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding

@Composable
fun StoryDetailScreen(
    viewModel: StoryDetailViewModel,
    onFinished: (newBadgeIds: List<Int>) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val storyWithOptions = state.storyWithOptions ?: return

    Column(modifier = Modifier.fillMaxSize().padding(ScreenPadding)) {
        Text(storyWithOptions.story.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(storyWithOptions.story.scenarioText, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))

        when (state.step) {
            StoryStep.EMOTION -> {
                Text(stringResource(R.string.story_question_emotion), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                storyWithOptions.emotionOptions.forEach { option ->
                    OptionCard(option.text) { viewModel.chooseEmotion(option.id) }
                    Spacer(Modifier.height(8.dp))
                }
            }
            StoryStep.REACTION -> {
                Text(stringResource(R.string.story_question_reaction), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                storyWithOptions.reactionOptions.forEach { option ->
                    OptionCard(option.text) { viewModel.chooseReaction(option.id) }
                    Spacer(Modifier.height(8.dp))
                }
            }
            StoryStep.FEEDBACK -> {
                val feedbackRes = if (state.wasReactionRecommended) R.string.story_feedback_good else R.string.story_feedback_try_again
                Text(stringResource(feedbackRes), style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(24.dp))
                MoodPrimaryButton(
                    text = stringResource(R.string.story_next),
                    onClick = { onFinished(state.newlyEarnedBadgeIds) }
                )
            }
        }
    }
}

@Composable
private fun OptionCard(text: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
    }
}
