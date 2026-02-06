package com.bor96dev.workmanagerdz

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bor96dev.workmanagerdz.workers.DownloadImageWorker

class ImageRotationRepository(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun createDownloadWork(url: String): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DownloadImageWorker>()
            .setInputData(
                workDataOf(WorkConstants.IMAGE_URL_KEY to url)
            )
            .build()
    }
}