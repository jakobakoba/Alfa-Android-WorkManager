package com.bor96dev.workmanagerdz.repository

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bor96dev.workmanagerdz.WorkConstants
import com.bor96dev.workmanagerdz.workers.DownloadImageWorker
import com.bor96dev.workmanagerdz.workers.ImageRotationWorker
import kotlinx.coroutines.flow.Flow

class ImageRotationRepository(private val context: Context) {

    private val workManager = WorkManager.Companion.getInstance(context)

    fun createDownloadWork(url: String): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DownloadImageWorker>()
            .setInputData(
                workDataOf(WorkConstants.IMAGE_URL_KEY to url)
            )
            .build()
    }

    fun createRotationWork(inputFilePath: String): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<ImageRotationWorker>()
            .setInputData(
                workDataOf(WorkConstants.OUTPUT_URI_KEY to inputFilePath,
                    )
            )
            .build()
    }

    fun getWorkInfosForUniqueWork(workName: String): Flow<List<WorkInfo>> {
        return workManager.getWorkInfosForUniqueWorkFlow(workName)
    }


}