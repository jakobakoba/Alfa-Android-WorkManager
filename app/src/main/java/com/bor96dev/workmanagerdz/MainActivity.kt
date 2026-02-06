package com.bor96dev.workmanagerdz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bor96dev.workmanagerdz.ui.theme.WorkManagerDzTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerDzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageRotationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

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
        Row {
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
    }
}

@Composable
private fun OriginalImageSection(imageUri: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Original Image",
                style = MaterialTheme.typography.titleMedium
            )

            val file = File(imageUri)
            android.util.Log.d("OriginalImageSection", "Trying to load original image from: $imageUri, file exists: ${file.exists()}")
            if (file.exists() && file.length() > 0) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(file)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Original downloaded image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text("Image file not found")
            }
        }
    }
}

@Composable
private fun ResultImageSection(
    imageUri: String,
    isProcessing: Boolean,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isProcessing) "Processing..." else "Processed Image",
                style = MaterialTheme.typography.titleMedium
            )

            val file = File(imageUri)
            if (file.exists() && file.length() > 0) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(file)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Processed image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )

                if (!isProcessing) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Image")
                    }
                }
            } else {
                Text("Processed image file not found or empty: $imageUri")
            }
        }
    }
}
