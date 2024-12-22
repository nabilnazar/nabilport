package com.nabilnazar.offlinefirstapp

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
 import androidx.work.WorkerParameters
import com.nabilnazar.offlinefirstapp.data.repository.CalculationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CalculationRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            repository.syncWithRemote()
            Log.d("SyncWorker", "Work executed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during sync", e)
            Result.failure()
        }
    }
}
