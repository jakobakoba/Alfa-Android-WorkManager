package com.bor96dev.workmanagerdz.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bor96dev.workmanagerdz.data.ImageRotationState
import com.bor96dev.workmanagerdz.data.ProcessingStep

@Composable
fun ProcessingProgressSection(state: ImageRotationState) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when (state.currentStep) {
                    ProcessingStep.DOWNLOADING -> "Downloading image..."
                    ProcessingStep.APPLYING_ROTATION -> "Applying rotation..."
                    ProcessingStep.UPLOADING -> "Uploading image..."
                    ProcessingStep.COMPLETED -> "Completed!"
                    ProcessingStep.IDLE -> ""
                },
                style = MaterialTheme.typography.bodyLarge
            )

            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${(state.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
