package com.kidslab.moodplanet.ui.breathing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kidslab.moodplanet.R
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle

@Composable
fun BreathingScreen(
    viewModel: BreathingViewModel,
    onFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    val targetScale = when (state.phase) {
        BreathingPhase.INHALE -> 1f
        BreathingPhase.HOLD -> 1f
        BreathingPhase.EXHALE -> 0.55f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = state.phase.seconds * 1000),
        label = "breathing-scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SectionTitle(stringResource(R.string.breathing_title))
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.breathing_round, state.round, 3),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(32.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
            Box(
                modifier = Modifier
                    .size(220.dp * scale.coerceIn(0.4f, 1f))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f), CircleShape)
            )
            if (!state.isFinished) {
                Text(
                    text = phaseLabel(state.phase),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        if (!state.isFinished) {
            Text("${state.secondsRemainingInPhase + 1}", style = MaterialTheme.typography.headlineMedium)
        } else {
            Text(stringResource(R.string.breathing_finish), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))
            MoodPrimaryButton(text = stringResource(R.string.continue_button), onClick = onFinished)
        }
    }
}

@Composable
private fun phaseLabel(phase: BreathingPhase): String = when (phase) {
    BreathingPhase.INHALE -> stringResource(R.string.breathing_inhale)
    BreathingPhase.HOLD -> stringResource(R.string.breathing_hold)
    BreathingPhase.EXHALE -> stringResource(R.string.breathing_exhale)
}
