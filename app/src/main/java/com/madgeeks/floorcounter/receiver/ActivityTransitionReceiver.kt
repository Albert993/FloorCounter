package com.madgeeks.floorcounter.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.madgeeks.floorcounter.BuildConfig
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.Utils.log
import com.madgeeks.floorcounter.Utils.getPendingIntentFlag
import com.madgeeks.floorcounter.service.ActivityTransitionUtil

data class ActivityType (
    val type: Int,
    val transitionType: Int
)

class ActivityTransitionReceiver: BroadcastReceiver() {
    companion object {
        const val ACTIVITY_INTENT_ACTION = "${BuildConfig.APPLICATION_ID}_transitions_receiver_action"

        fun getPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.activityRecognitionRequestCode,
            Intent(ACTIVITY_INTENT_ACTION),
            getPendingIntentFlag())
    }

    var action: ((ActivityType) -> Unit)? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (!ActivityTransitionResult.hasResult(p1)) return

        p1?.let { intent ->
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let {
                it.transitionEvents.forEach { event ->
                    log("Activity: ${ActivityTransitionUtil.getActivityString(event.activityType)}, type: ${ActivityTransitionUtil.getTransitionTypeString(event.transitionType)}")
                    action?.invoke(ActivityType(event.activityType, event.transitionType))
                }
            }
        }
    }
}