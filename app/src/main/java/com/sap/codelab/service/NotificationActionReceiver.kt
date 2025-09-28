package com.sap.codelab.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sap.codelab.repository.Repository
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.view.detail.BUNDLE_MEMO_ID
import com.sap.codelab.view.detail.ViewMemo
import kotlinx.coroutines.launch

/**
 * Handles notification actions (View, Mark Done, Snooze).
 */
class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NotificationActionReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationHelper.ACTION_VIEW_MEMO -> {
                val memoId = intent.getLongExtra(BUNDLE_MEMO_ID, -1)
                if (memoId != -1L) {
                    openMemo(context, memoId)
                }
            }
            
            NotificationHelper.ACTION_MARK_DONE -> {
                val memoId = intent.getLongExtra(BUNDLE_MEMO_ID, -1)
                if (memoId != -1L) {
                    markMemoAsDone(memoId)
                }
            }
            
            NotificationHelper.ACTION_SNOOZE -> {
                val memoId = intent.getLongExtra(BUNDLE_MEMO_ID, -1)
                if (memoId != -1L) {
                    snoozeMemo(context, memoId)
                }
            }
        }
    }

    private fun openMemo(context: Context, memoId: Long) {
        try {
            val intent = Intent(context, ViewMemo::class.java).apply {
                putExtra(BUNDLE_MEMO_ID, memoId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            
            // Cancel the notification since user is viewing the memo
            NotificationHelper(context).cancelMemoNotification(memoId)
            
            Log.d(TAG, "Opened memo: $memoId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open memo: ${e.message}", e)
        }
    }

    private fun markMemoAsDone(memoId: Long) {
        ScopeProvider.application.launch {
            try {
                Repository.markMemoAsDone(memoId)
                Log.d(TAG, "Marked memo as done: $memoId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to mark memo as done: ${e.message}", e)
            }
        }
    }

    private fun snoozeMemo(context: Context, memoId: Long) {
        // For now, just cancel the notification
        // In a real app, you'd implement actual snooze functionality
        NotificationHelper(context).cancelMemoNotification(memoId)
        Log.d(TAG, "Snoozed memo: $memoId")
    }
}
