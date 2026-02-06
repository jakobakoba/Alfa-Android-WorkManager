package com.bor96dev.workmanagerdz.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bor96dev.workmanagerdz.ImageRotationViewModel
import com.bor96dev.workmanagerdz.screens.composables.LoadingOriginalImageSection
import com.bor96dev.workmanagerdz.screens.composables.OriginalImageSection
import com.bor96dev.workmanagerdz.screens.composables.ProcessingProgressSection
import com.bor96dev.workmanagerdz.screens.composables.ResultImageSection

@Composable
fun ImageRotationScreen(
    viewModel: ImageRotationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = state.imageUrl,
                onValueChange = { viewModel.updateImageUrl(it) },
                label = { Text("Image URL") },
                placeholder = { Text("https://images.pexels.com/photos/7524329/pexels-photo-7524329.jpeg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = state.error?.isNotBlank() == true
            )
            Spacer(modifier = Modifier.width(16.dp))
            val clipboardManager = LocalClipboardManager.current
            Button(
                onClick = {
                    val clipboardText = clipboardManager.getText()?.text ?: ""
                    if (clipboardText.isNotBlank()) {
                        viewModel.updateImageUrl(clipboardText)
                    }
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Вставить")
            }
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (state.isLoadingOriginal) {
            LoadingOriginalImageSection()
        } else {
            state.downloadedImageUri?.let { uri ->
                OriginalImageSection(imageUri = uri)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        if (state.downloadedImageUri == null && !state.isLoadingOriginal && state.imageUrl.isNotBlank()) {
            Button(
                onClick = { viewModel.updateImageUrl(state.imageUrl) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download Image")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (state.isRotating) {
                    viewModel.cancelProcessing()
                } else {
                    viewModel.startImageRotation()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.downloadedImageUri != null && !state.isRotating && !state.isLoadingOriginal
        ) {
            Text(
                if (state.isRotating) "Cancel Processing" else "Send & Get a rotated Image"
            )
        }

        if (state.isRotating) {
            ProcessingProgressSection(state = state)
        }

        state.rotatedImageUri?.let { uri ->
            ResultImageSection(
                imageUri = uri,
                isRotating = state.isRotating
            )
        }
    }
}
