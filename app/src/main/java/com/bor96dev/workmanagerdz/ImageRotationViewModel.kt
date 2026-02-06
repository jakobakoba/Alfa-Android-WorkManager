package com.bor96dev.workmanagerdz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImageRotationViewModel(application : Application): AndroidViewModel(application) {

    private val repository = ImageRotationRepository(application)

    private val _state = MutableStateFlow(ImageRotationState())
    val state: StateFlow<ImageRotationState> = _state.asStateFlow()





}