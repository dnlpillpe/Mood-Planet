package com.kidslab.moodplanet.ui.calmvoice

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kidslab.moodplanet.R
import com.kidslab.moodplanet.audio.CalmVoiceRecorder
import com.kidslab.moodplanet.ui.calmvoice.PermissionState
import com.kidslab.moodplanet.ui.components.EducationalDisclaimer
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle

@Composable
fun CalmVoiceScreen(
    viewModel: CalmVoiceViewModel,
    onFinished: (newBadgeIds: List<Int>) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(granted) }

    LaunchedEffect(state.justFinished) {
        if (state.justFinished) onFinished(state.newlyEarnedBadgeIds)
    }

    Column(modifier = Modifier.fillMaxSize().padding(ScreenPadding)) {
        SectionTitle(stringResource(R.string.calm_voice_title))
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.calm_voice_instructions), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        EducationalDisclaimer(text = stringResource(R.string.calm_voice_privacy_note))
        Spacer(Modifier.height(24.dp))

        if (state.permissionState == PermissionState.DENIED && !state.isActive) {
            Text(stringResource(R.string.calm_voice_permission_denied), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
        }

        if (state.isActive) {
            if (!state.isPracticingWithoutMic) {
                VolumeMeter(level = state.volumeLevel)
            } else {
                Text("🌬️ Practicando sin micrófono…", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(16.dp))
            Text("${state.elapsedSeconds} s", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            MoodPrimaryButton(text = stringResource(R.string.calm_voice_stop), onClick = viewModel::finish)
        } else {
            MoodPrimaryButton(
                text = stringResource(R.string.calm_voice_start),
                onClick = {
                    if (CalmVoiceRecorder.hasPermission(context)) {
                        viewModel.startWithMic()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = viewModel::startWithoutMic, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.calm_voice_no_mic))
            }
        }
    }
}

@Composable
private fun VolumeMeter(level: Float) {
    val animatedLevel by animateFloatAsState(targetValue = level.coerceIn(0f, 1f), label = "volume-level")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (0.05f + animatedLevel * 0.95f).coerceIn(0.05f, 1f))
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp))
            )
        }
    }
}
