package com.bor96dev.workmanagerdz.workers

import android.content.Context
import android.graphics.BitmapFactory
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bor96dev.workmanagerdz.WorkConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class DownloadImageWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val imageUrl = inputData.getString(WorkConstants.IMAGE_URL_KEY)
                ?: return@withContext Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Image URL is required")
                )

            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()

            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                return@withContext Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Failed to decode image")
                )
            }

            val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            java.io.FileOutputStream(file).use { out ->
                val compressed = bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                android.util.Log.d("DownloadWorker", "Compression successful: $compressed")
            }

            if (file.exists() && file.length() > 0) {
                Result.success(
                    workDataOf(WorkConstants.OUTPUT_URI_KEY to file.absolutePath)
                )
            } else {
                Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Failed to save downloaded image")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                workDataOf(WorkConstants.ERROR_MESSAGE_KEY to e.localizedMessage)
            )
        }
    }
}