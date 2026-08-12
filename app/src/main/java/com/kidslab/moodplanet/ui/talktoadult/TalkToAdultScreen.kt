package com.kidslab.moodplanet.ui.talktoadult

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kidslab.moodplanet.R
import com.kidslab.moodplanet.ui.components.MoodPrimaryButton
import com.kidslab.moodplanet.ui.components.ScreenPadding
import com.kidslab.moodplanet.ui.components.SectionTitle

@Composable
fun TalkToAdultScreen(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(ScreenPadding)) {
        SectionTitle(stringResource(R.string.talk_adult_title))
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.talk_adult_body), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))
        Text("🧑‍🤝‍🧑 💜", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        MoodPrimaryButton(text = stringResource(R.string.close), onClick = onClose)
    }
}
