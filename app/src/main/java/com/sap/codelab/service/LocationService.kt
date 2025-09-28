package com.sap.codelab.service

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.di.SimpleDIContainer
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.utils.DebugHelper
import kotlinx.coroutines.launch

/**
 * Background service that monitors user location and triggers notifications
 * when the user is within 200 meters of a memo's location.
 */
class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationHelper: NotificationHelper
    private var locationCallback: LocationCallback? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_service_channel"
        private const val PROXIMITY_THRESHOLD_METERS = 200.0
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationHelper = NotificationHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notificationHelper.showLocationServiceNotification().build())
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null



    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).apply {
            setMinUpdateIntervalMillis(5000L) // 5 seconds
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    checkProximityToMemos(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    private fun checkProximityToMemos(currentLocation: Location) {
        ScopeProvider.application.launch {
            try {
                val repository = SimpleDIContainer.getMemoRepository()
                val result = repository.getAllMemos()
                
                when (result) {
                    is com.sap.codelab.utils.Result.Success -> {
                        val memos = result.data
                        Log.d("LocationService", "Checking ${memos.size} memos for proximity")
                        
                        memos.forEach { memo ->
                            if (DebugHelper.shouldTriggerNotification(currentLocation, memo)) {
                                Log.d("LocationService", "Triggering notification for memo: ${memo.title}")
                                showMemoNotification(memo)
                                // Mark memo as done to prevent duplicate notifications
                                repository.markMemoAsDone(memo.id)
                            }
                        }
                    }
                    is com.sap.codelab.utils.Result.Error -> {
                        Log.e("LocationService", "Error getting memos: ${result.appError.getUserMessage()}")
                    }
                    is com.sap.codelab.utils.Result.Loading -> {
                        Log.d("LocationService", "Loading memos...")
                    }
                }
            } catch (e: Exception) {
                Log.e("LocationService", "Error checking proximity: ${e.message}", e)
            }
        }
    }

    private fun showMemoNotification(memo: MemoEntity) {
        val notificationText = if (memo.description.length > 140) {
            memo.description.substring(0, 140) + "..."
        } else {
            memo.description
        }

        notificationHelper.showMemoNotification(
            memo.id,
            memo.title,
            notificationText
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}
