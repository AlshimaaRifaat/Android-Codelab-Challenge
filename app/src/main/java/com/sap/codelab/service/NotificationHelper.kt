package com.sap.codelab.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sap.codelab.R
import com.sap.codelab.view.detail.ViewMemo
import com.sap.codelab.view.detail.BUNDLE_MEMO_ID

/**
 * Production-ready notification helper with comprehensive features.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        // Notification channels
        private const val CHANNEL_ID_MEMO_REMINDERS = "memo_reminders"
        private const val CHANNEL_ID_LOCATION_SERVICE = "location_service"
        
        // Notification IDs
        private const val NOTIFICATION_ID_LOCATION_SERVICE = 1001
        private const val NOTIFICATION_ID_MEMO_BASE = 2000
        
        // Actions (public so NotificationActionReceiver can access them)
        const val ACTION_VIEW_MEMO = "action_view_memo"
        const val ACTION_MARK_DONE = "action_mark_done"
        const val ACTION_SNOOZE = "action_snooze"
        
        // Request codes
        private const val REQUEST_CODE_VIEW_MEMO = 1001
        private const val REQUEST_CODE_MARK_DONE = 1002
        private const val REQUEST_CODE_SNOOZE = 1003
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            
            // Memo reminders channel
            val memoChannel = NotificationChannel(
                CHANNEL_ID_MEMO_REMINDERS,
                "Memo Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Location-based memo reminders"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            
            // Location service channel
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_LOCATION_SERVICE,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background location monitoring"
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
            }
            
            notificationManager.createNotificationChannel(memoChannel)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    /**
     * Shows a memo reminder notification with actions.
     */
    fun showMemoNotification(memoId: Long, title: String, content: String) {
        try {
            val notificationId = (NOTIFICATION_ID_MEMO_BASE + memoId).toInt()
            
            val notification = NotificationCompat.Builder(context, CHANNEL_ID_MEMO_REMINDERS)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(createViewMemoIntent(memoId, notificationId))
                .addAction(createViewMemoAction(memoId, notificationId))
                .addAction(createMarkDoneAction(memoId, notificationId))
                .addAction(createSnoozeAction(memoId, notificationId))
                .setGroup("memo_reminders")
                .setGroupSummary(false)
                .build()

            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                NotificationManagerCompat.from(context).notify(notificationId, notification)
            }
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "Failed to show memo notification: ${e.message}", e)
        }
    }

    /**
     * Shows the location service notification.
     */
    fun showLocationServiceNotification(): NotificationCompat.Builder {
        val intent = Intent(context, com.sap.codelab.view.home.Home::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_LOCATION_SERVICE)
            .setContentTitle("Location Monitoring")
            .setContentText("Monitoring your location for memo reminders")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
    }

    private fun createViewMemoIntent(memoId: Long, notificationId: Int): PendingIntent {
        val intent = Intent(context, ViewMemo::class.java).apply {
            putExtra(BUNDLE_MEMO_ID, memoId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        return PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createViewMemoAction(memoId: Long, notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_VIEW_MEMO
            putExtra(BUNDLE_MEMO_ID, memoId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE_VIEW_MEMO, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            "View",
            pendingIntent
        ).build()
    }

    private fun createMarkDoneAction(memoId: Long, notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(BUNDLE_MEMO_ID, memoId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE_MARK_DONE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            "Done",
            pendingIntent
        ).build()
    }

    private fun createSnoozeAction(memoId: Long, notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(BUNDLE_MEMO_ID, memoId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE_SNOOZE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            "Snooze",
            pendingIntent
        ).build()
    }

    /**
     * Cancels a specific memo notification.
     */
    fun cancelMemoNotification(memoId: Long) {
        val notificationId = (NOTIFICATION_ID_MEMO_BASE + memoId).toInt()
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    /**
     * Cancels all memo notifications.
     */
    fun cancelAllMemoNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}