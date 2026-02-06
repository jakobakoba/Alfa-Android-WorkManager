package com.bor96dev.workmanagerdz.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bor96dev.workmanagerdz.data.WorkConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageRotationWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val inputUri = inputData.getString(WorkConstants.OUTPUT_URI_KEY)
                ?: return@withContext Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Input image URI is required")
                )

            val inputFile = File(inputUri)
            if (!inputFile.exists()) {
                return@withContext Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Input image file not found")
                )
            }
            val bitmap = BitmapFactory.decodeFile(inputUri)
                ?: return@withContext Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Failed to decode image from: $inputUri")
                )

            val rotatedBitmap = applyRotation(bitmap)


            val outputFileName = "filtered_image_${System.currentTimeMillis()}.jpg"
            val outputFile = File(context.filesDir, outputFileName)
            FileOutputStream(outputFile).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            if (outputFile.exists() && outputFile.length() > 0) {
                inputFile.delete()
                android.util.Log.d(
                    "ApplyFilterWorker",
                    "Original file deleted: ${inputFile.absolutePath}"
                )

                Result.success(
                    workDataOf(WorkConstants.OUTPUT_URI_KEY to outputFile.absolutePath)
                )
            } else {
                android.util.Log.e("ApplyFilterWorker", "Failed to save filtered image")
                Result.failure(
                    workDataOf(WorkConstants.ERROR_MESSAGE_KEY to "Failed to save filtered image")
                )
            }

        } catch (e: Exception) {
            Result.failure(
                workDataOf(WorkConstants.ERROR_MESSAGE_KEY to e.localizedMessage)
            )
        }
    }

    private fun applyRotation(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}