package com.bor96dev.workmanagerdz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageRotationViewModel(application : Application): AndroidViewModel(application) {

    private val repository = ImageRotationRepository(application)

    private val _state = MutableStateFlow(ImageRotationState())
    val state: StateFlow<ImageRotationState> = _state.asStateFlow()


    fun updateImageUrl(url: String) {
        _state.value = _state.value.copy(
            imageUrl = url
        )

        if (url.isNotBlank()){
            downloadOriginalImage(url)
        }
    }

    private fun downloadOriginalImage(url: String){

        viewModelScope.launch{
            try {
                val downloadWork = repository.createDownloadWork(url)
            }
        }
    }



}