package com.labb.vishinandroid.domain.repositories

import android.util.Log
import com.labb.vishinandroid.data.supabase.SupabaseClientProvider
import com.labb.vishinandroid.domain.model.LowConfidenceFraudEvent
import io.github.jan.supabase.postgrest.from

class SupabaseFraudEventRepository(
    private val tableName: String = "fraud_events"
) : FraudEventRepository {

    companion object {
        private const val TAG = "SupabaseFraudRepo"
    }

    override suspend fun saveLowConfidenceEvent(event: LowConfidenceFraudEvent) {
        runCatching {
            SupabaseClientProvider.client
                .from(tableName)
                .insert(event)
        }.onFailure { e ->
            Log.w(TAG, "Failed to insert low-confidence event", e)
        }
    }
}
