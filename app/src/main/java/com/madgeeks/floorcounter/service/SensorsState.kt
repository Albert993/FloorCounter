package com.madgeeks.floorcounter.service

import androidx.lifecycle.MutableLiveData
import com.madgeeks.floorcounter.receiver.ActivityType

object SensorsState {
    var barometerReading = MutableLiveData<Float>()
    var currentAltitude = MutableLiveData<Float>()
    var maxAltitudeChange = MutableLiveData<Float>()
    var currentActivityType = MutableLiveData<ActivityType>()
}