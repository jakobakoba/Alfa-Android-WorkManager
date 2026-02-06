package com.bor96dev.workmanagerdz.data

import androidx.compose.runtime.Immutable

@Immutable
data class ImageRotationState (
    val imageUrl: String = "https://img.freepik.com/free-photo/portrait-cute-dog-anime-style_23-2151382120.jpg",
    val isLoadingOriginal: Boolean = false,
    val isRotating: Boolean = false,
    val progress: Float = 0f,
    val currentStep: ProcessingStep = ProcessingStep.IDLE,
    val error: String? = null,
    val downloadedImageUri: String? = null,
    val rotatedImageUri: String? = null
)

enum class ProcessingStep {
    IDLE,
    DOWNLOADING,
    APPLYING_ROTATION,
    UPLOADING,
    COMPLETED
}