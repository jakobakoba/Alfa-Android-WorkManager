package com.bor96dev.workmanagerdz

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ImageRotationViewModel(application : Application): AndroidViewModel(application) {

    private val repository = ImageRotationRepository(application)

    private val _state = MutableStateFlow()



}