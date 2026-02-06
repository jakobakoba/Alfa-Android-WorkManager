package com.bor96dev.workmanagerdz

import androidx.compose.runtime.Immutable

@Immutable
data class ImageRotationState (
    val imageUrl: String = "https://images.pexels.com/photos/7524329/pexels-photo-7524329.jpeg",
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
    COMPLETED
}