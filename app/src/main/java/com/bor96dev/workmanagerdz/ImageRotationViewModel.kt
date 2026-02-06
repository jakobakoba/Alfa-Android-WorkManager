package com.bor96dev.workmanagerdz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.bor96dev.workmanagerdz.repository.ImageRotationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageRotationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageRotationRepository(application)

    private val _state = MutableStateFlow(ImageRotationState())
    val state: StateFlow<ImageRotationState> = _state.asStateFlow()


    fun updateImageUrl(url: String) {
        _state.value = _state.value.copy(
            imageUrl = url
        )

        if (url.isNotBlank()) {
            downloadOriginalImage(url)
        }
    }

    private fun downloadOriginalImage(url: String) {
        viewModelScope.launch {
            try {
                val downloadWork = repository.createDownloadWork(url)
                repository.enqueueDownloadWork(downloadWork)

                repository.getWorkInfosForUniqueWork(WorkConstants.IMAGE_PROCESSING_WORK_CHAIN)
                    .collect { workInfos ->
                        val downloadWorkInfo = workInfos.firstOrNull {
                            it.tags.contains("com.bor96dev.workmanagerdz.workers.DownloadImageWorker")
                        }

                        if (downloadWorkInfo != null) {
                            when (downloadWorkInfo.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    val downloadedUri = downloadWorkInfo.outputData.getString(
                                        WorkConstants.OUTPUT_URI_KEY
                                    )
                                    _state.value = _state.value.copy(
                                        isLoadingOriginal = false,
                                        downloadedImageUri = downloadedUri
                                    )
                                    return@collect
                                }

                                WorkInfo.State.FAILED -> {
                                    val errorMessage =
                                        downloadWorkInfo.outputData.getString(WorkConstants.ERROR_MESSAGE_KEY)
                                            ?: "Failed to download image"
                                    _state.value = _state.value.copy(
                                        isLoadingOriginal = false,
                                        error = errorMessage
                                    )
                                    return@collect
                                }

                                else -> {

                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingOriginal = false,
                    error = "Failed to load original image: ${e.message}"
                )
            }
        }
    }

    fun startImageRotation(){
        val currentState = _state.value
        if (currentState.downloadedImageUri.isNullOrBlank()) {
            _state.value = currentState.copy(error = "Please wait for image to load or enter a valid URL")
            return
        }

        if (currentState.isRotating) return

        viewModelScope.launch{
            _state.value = currentState.copy(
                isRotating = true,
                progress = 0f,
                currentStep = ProcessingStep.APPLYING_ROTATION,
                error = null,
                rotatedImageUri = null
            )

            try {
                observeRotationWorkProgress()
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isRotating = false,
                    error = e.localizedMessage ?: "Unknown error occurred"
                )
            }
        }
    }

    private fun observeRotationWorkProgress(){
        viewModelScope.launch {
            repository.getWorkInfosForUniqueWork("rotation_work_unique")
                .collect {workInfos ->
                    val filterWork = workInfos.firstOrNull{
                        it.tags.contains("com.bor96dev.workmanagerdz.workers.ImageRotationWorker")
                    }

                    if (filterWork != null) {
                        when (filterWork.state) {
                            WorkInfo.State.RUNNING -> {
                                _state.value = _state.value.copy (
                                    progress = 0.8f,
                                    currentStep = ProcessingStep.APPLYING_ROTATION
                                )
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                val resultUri = filterWork.outputData.getString(WorkConstants.OUTPUT_URI_KEY)
                                _state.value = _state.value.copy(
                                    isRotating = false,
                                    progress = 1f,
                                    currentStep = ProcessingStep.COMPLETED,
                                    rotatedImageUri = resultUri
                                )
                            }

                            WorkInfo.State.FAILED -> {
                                val errorMessage = filterWork.outputData.getString(WorkConstants.ERROR_MESSAGE_KEY)
                                    ?: "Filter processing failed"
                                _state.value = _state.value.copy(
                                    isRotating = false,
                                    progress = 0f,
                                    currentStep = ProcessingStep.IDLE,
                                    error = errorMessage
                                )
                            }
                            WorkInfo.State.CANCELLED -> {
                                _state.value = _state.value.copy(
                                    isRotating = false,
                                    progress = 0f,
                                    currentStep = ProcessingStep.IDLE,
                                    error = "Processing cancelled"
                                )
                            }
                            else -> {}
                        }
                    }
                }
        }
    }

    fun cancelProcessing() {
        repository.cancelWork("filter_work_unique")
        repository.cancelWork(WorkConstants.IMAGE_PROCESSING_WORK_CHAIN)
        _state.value = _state.value.copy(
            isLoadingOriginal = false,
            isRotating = false,
            progress = 0f,
            currentStep = ProcessingStep.IDLE,
            downloadedImageUri = null,
            rotatedImageUri = null,
            error = "Processing cancelled"
        )
    }
}