package com.sap.codelab.utils

import android.content.Context
import android.location.Location
import android.util.Log
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Debug helper for testing location-based notifications.
 */
object DebugHelper {
    
    private const val TAG = "DebugHelper"
    
    /**
     * Log all memos with their location data for debugging.
     */
    fun logAllMemos(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val memos = Repository.getAllMemos()
                Log.d(TAG, "=== ALL MEMOS ===")
                memos.forEach { memo ->
                    Log.d(TAG, "Memo ID: ${memo.id}")
                    Log.d(TAG, "Title: ${memo.title}")
                    Log.d(TAG, "Description: ${memo.description}")
                    Log.d(TAG, "Latitude: ${memo.reminderLatitude}")
                    Log.d(TAG, "Longitude: ${memo.reminderLongitude}")
                    Log.d(TAG, "Is Done: ${memo.isDone}")
                    Log.d(TAG, "---")
                }
                Log.d(TAG, "Total memos: ${memos.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error logging memos: ${e.message}")
            }
        }
    }
    
    /**
     * Log current location for debugging.
     */
    fun logCurrentLocation(location: Location) {
        Log.d(TAG, "Current location: ${location.latitude}, ${location.longitude}")
        Log.d(TAG, "Accuracy: ${location.accuracy}m")
        Log.d(TAG, "Provider: ${location.provider}")
    }
    
    /**
     * Check if location service should trigger notification for a memo.
     */
    fun shouldTriggerNotification(currentLocation: Location, memo: Memo): Boolean {
        if (memo.isDone) {
            Log.d(TAG, "Memo '${memo.title}' is already done, skipping")
            return false
        }
        
        if (memo.reminderLatitude == 0.0 || memo.reminderLongitude == 0.0) {
            Log.d(TAG, "Memo '${memo.title}' has no location data, skipping")
            return false
        }
        
        val memoLocation = Location("memo").apply {
            latitude = memo.reminderLatitude
            longitude = memo.reminderLongitude
        }
        
        val distance = currentLocation.distanceTo(memoLocation)
        val shouldTrigger = distance <= 200.0
        
        Log.d(TAG, "Memo '${memo.title}': distance=${distance}m, shouldTrigger=$shouldTrigger")
        
        return shouldTrigger
    }
    
    /**
     * Log notification channel status.
     */
    fun logNotificationChannels(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channels = notificationManager.notificationChannels
            Log.d(TAG, "=== NOTIFICATION CHANNELS ===")
            channels.forEach { channel ->
                Log.d(TAG, "Channel ID: ${channel.id}")
                Log.d(TAG, "Channel Name: ${channel.name}")
                Log.d(TAG, "Importance: ${channel.importance}")
                Log.d(TAG, "Enabled: ${channel.importance != android.app.NotificationManager.IMPORTANCE_NONE}")
                Log.d(TAG, "---")
            }
        }
    }
    
    /**
     * Log location permissions status.
     */
    fun logLocationPermissions(context: Context) {
        val fineLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val backgroundLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        
        Log.d(TAG, "=== LOCATION PERMISSIONS ===")
        Log.d(TAG, "Fine Location: ${if (fineLocation == android.content.pm.PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"}")
        Log.d(TAG, "Coarse Location: ${if (coarseLocation == android.content.pm.PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"}")
        Log.d(TAG, "Background Location: ${if (backgroundLocation == android.content.pm.PackageManager.PERMISSION_GRANTED) "GRANTED" else "DENIED"}")
    }
}

