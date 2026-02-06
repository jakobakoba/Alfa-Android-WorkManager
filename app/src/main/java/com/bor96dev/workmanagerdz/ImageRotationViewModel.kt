package com.bor96dev.workmanagerdz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
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
}