package com.madgeeks.floorcounter.service

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity
import com.madgeeks.floorcounter.receiver.ActivityTransitionReceiver
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.Utils.log
import com.madgeeks.floorcounter.Utils.getPendingIntentFlag
import com.madgeeks.floorcounter.data.db.Hour
import com.madgeeks.floorcounter.data.db.MainViewModelRepository
import com.madgeeks.floorcounter.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ForegroundService: Service() {
    private val sensorManager: SensorManager  by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val pressureSensor : Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) as Sensor
    }

    private val transitionBroadcastReceiver: ActivityTransitionReceiver = ActivityTransitionReceiver().apply {
        action = { activity ->
            SensorsState.currentActivityType.postValue(activity)

            when(activity.type) {
                DetectedActivity.RUNNING,
                DetectedActivity.WALKING -> {
                    if (activity.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        sensorManager.registerListener(pressureSensorListener, pressureSensor, PressureSensorListener.SAMPLING_RATE)
                    else
                        sensorManager.unregisterListener(pressureSensorListener)
                }
                else -> sensorManager.unregisterListener(pressureSensorListener)
            }
        }
    }

    private val pressureSensorListener by lazy { getKoin().get<PressureSensorListener>() }
    private val notifications by lazy { getKoin().get<Notifications>() }
    private val activityRecognitionClient by lazy { getKoin().get<ActivityRecognitionClient>() }
    private val repository by lazy { getKoin().get<MainViewModelRepository>() }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient
                .requestActivityTransitionUpdates(ActivityTransitionUtil.getTransitionRequest(), ActivityTransitionReceiver.getPendingIntent(this))
                .addOnSuccessListener { log("Registered for activity recognition") }
                .addOnFailureListener { log("Failed registering for activity recognition") }
        }

        pressureSensorListener.setOnAltitudeChanged { hpa, meters ->
            log("Current altitude: $meters meters")
            SensorsState.barometerReading.postValue(hpa)
            SensorsState.currentAltitude.postValue(meters)
            SensorsState.maxAltitudeChange.postValue(pressureSensorListener.maxAltitudeChange)
        }

        pressureSensorListener.setOnFloorClimbed { floors ->
            log("Floors Climbed: $floors")
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(Hour(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT_HOUR)), floors))
            }
        }

        registerReceiver(transitionBroadcastReceiver, IntentFilter(ActivityTransitionReceiver.ACTIVITY_INTENT_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(transitionBroadcastReceiver)
        sensorManager.unregisterListener(pressureSensorListener)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED)
            activityRecognitionClient.removeActivityTransitionUpdates(ActivityTransitionReceiver.getPendingIntent(this))
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra(Constants.inputExtra)
        val notificationIntent = Intent(this, MainActivity::class.java)

        startForeground(
            Constants.jobId,
            notifications.create(PendingIntent.getActivity(this, 0, notificationIntent, getPendingIntentFlag()), input ?: "")
        )

        log("Started service")

        return START_NOT_STICKY
    }
}