package com.nabilnazar.deck69coroutineexplorer.usecases


import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import android.util.Log

object CoroutineUseCase {

    private const val TAG = "CoroutineUseCase"

    fun performBackgroundTask(): String {
        Log.d(TAG, "Background task started")
        Thread.sleep(5000)
        Log.d(TAG, "Background task completed")
        return "Task done in background"
    }

    suspend fun parallelTasks(): List<String> {
        return coroutineScope {
            Log.d(TAG, "Parallel tasks started")
            val task1 = async {
                delay(9000L)
                "Task 1 result"
            }
            val task2 = async {
                delay(5000L)
                "Task 2 result" }
            val result = listOf(task1.await(), task2.await())
            Log.d(TAG, "Parallel tasks completed: $result")
            result
        }
    }

    suspend fun errorHandlingDemo(): String {
        return try {
            Log.d(TAG, "Error handling demo started")
            withContext(Dispatchers.IO) {
                throw Exception("Simulated error")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error caught: ${e.message}")
            "Error handled: ${e.message}"
        }
    }

    fun cancellationDemo(scope: CoroutineScope): Pair<Job, MutableStateFlow<String>> {
        val stateFlow = MutableStateFlow("Starting task")
        val job = scope.launch {
            try {
                repeat(10) {
                    delay(1000)
                    stateFlow.value = "Working on step $it"
                    Log.d(TAG, "Step $it completed")
                }
                stateFlow.value = "Task completed"
            } catch (e: CancellationException) {
                Log.d(TAG, "Task cancelled")
                stateFlow.value = "Task cancelled"
            }
        }
        return Pair(job, stateFlow)
    }

    fun stateFlowDemo(): StateFlow<String> = MutableStateFlow("Initial value").apply {
        CoroutineScope(Dispatchers.Default).launch {
            delay(2000)
            emit("Updated value from StateFlow")
            Log.d(TAG, "StateFlow emitted updated value")
        }
    }

    fun sharedFlowDemo(scope: CoroutineScope): SharedFlow<String> {
        val sharedFlow = MutableSharedFlow<String>(replay = 1)
        scope.launch {
            repeat(5) { count ->
                sharedFlow.emit("SharedFlow emitted value #$count")
                Log.d(TAG, "SharedFlow emitted value #$count")
                delay(1000)
            }
        }
        return sharedFlow
    }
}
