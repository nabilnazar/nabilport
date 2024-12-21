package com.nabilnazar.offlinefirstapp.data.repository

import android.content.Context
import android.util.Log
import com.nabilnazar.offlinefirstapp.data.db.CalculationDao
import com.nabilnazar.offlinefirstapp.data.db.CalculationEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class CalculationRepository(
    private val dao: CalculationDao,
    private val supabase: SupabaseClient
) {
    val allCalculations: Flow<List<CalculationEntity>> = dao.getAllCalculations()



    // Add a new calculation
    suspend fun addCalculation(input1: Double, input2: Double, result: Double) {
        val calculation = CalculationEntity(
            input1 = input1,
            input2 = input2,
            result = result,
            isSynced = false
        )
        dao.insert(calculation)
    }

    // Sync unsynced calculations with the remote database
    suspend fun syncWithRemote() {
        val unsyncedCalculations = dao.getAllCalculations().first().filter { !it.isSynced }

        unsyncedCalculations.forEach { calculation ->
            try {
                supabase.postgrest["calculation_history"].insert(
                    mapOf(
                        "input1" to calculation.input1,
                        "input2" to calculation.input2,
                        "result" to calculation.result
                    )
                )
                dao.markAsSynced(calculation.id) // Mark as synced after successful insert
            } catch (e: Exception) {
              Log.e("CalculationRepository", "Error syncing calculation: ${e.message}")

            }
        }
    }

    suspend fun subscribeToRealtimeUpdates() {
        // Create a channel for the "calculation_history" table
        val channel = supabase.realtime.channel("calculation_history") {
            // Optional configuration if needed
        }

        // Create a Flow to listen for INSERT events
        val insertFlow = channel.broadcastFlow<Map<String, Any>>(event = "INSERT")

        // Collect the Flow in a CoroutineScope
        CoroutineScope(Dispatchers.IO).launch {
            insertFlow.collect { event ->
                val input1 = (event["input1"] as? Number)?.toDouble() ?: 0.0
                val input2 = (event["input2"] as? Number)?.toDouble() ?: 0.0
                val result = (event["result"] as? Number)?.toDouble() ?: 0.0

                val calculation = CalculationEntity(
                    input1 = input1,
                    input2 = input2,
                    result = result,
                    isSynced = true
                )

                // Insert the new calculation into the local database
                dao.insert(calculation)
            }
        }

        // Subscribe to the channel
        channel.subscribe(blockUntilSubscribed = true)
    }


}
