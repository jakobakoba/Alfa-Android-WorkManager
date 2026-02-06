package com.bor96dev.workmanagerdz.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DownloadImageWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

}