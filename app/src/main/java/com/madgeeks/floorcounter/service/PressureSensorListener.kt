package com.madgeeks.floorcounter.service

import android.content.Context
import android.hardware.*
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.Utils.log
import com.madgeeks.floorcounter.data.remote.WeatherAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class PressureSensorListener(
    private val context: Context,
    locationListener: MyLocationCallback,
    weatherApi: WeatherAPI
): SensorEventListener {
    companion object {
        /*
            The desired delay between two consecutive events in microseconds.
            This is only a hint to the system.
            Events may be received faster or slower than the specified rate.
            Usually events are received faster.
            Expressed in microseconds
        */
        const val SAMPLING_RATE = 30000000

        // 1 floor = 3m
        private const val FLOOR_METERS_THRESHOLD = 3.0f
    }

    private var isUpdatingBasePressure = false

    /*
     The pressure at sea level must be known, usually it can be retrieved from airport
     databases in the vicinity or any weather api.
     Or use the GPS to get the altitude and use the "getSealevelPressure" method, but this
     is less accurate than weather stations or airport sensors method.
     */
    private var seaLevelPressure = SensorManager.PRESSURE_STANDARD_ATMOSPHERE
    private var prevMeasuredAltitude: Float? = null
    var maxAltitudeChange: Float = 0f

    private var onAltitudeChanged: ((Float, Float) -> Unit)? = null
    private var onFloorsClimbed: ((Float) -> Unit)? = null

    init {
        locationListener.setOnCoordinatesChanged { longitude, latitude ->
            isUpdatingBasePressure = true
            if (longitude == null || latitude == null) {
                seaLevelPressure = SensorManager.PRESSURE_STANDARD_ATMOSPHERE
                isUpdatingBasePressure = false
                return@setOnCoordinatesChanged
            }

            log("longitude = $longitude, latitude = $latitude")
            CoroutineScope(Dispatchers.IO).launch {
                try
                {
                    val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
                    val weatherParams = "pressure_msl"
                    val response = weatherApi.getWeather(latitude.toString(), longitude.toString(), weatherParams, date, date)

                    response.body()?.let {
                        seaLevelPressure = if (response.isSuccessful)
                            it.getSeaLevelPressure() ?: SensorManager.PRESSURE_STANDARD_ATMOSPHERE
                        else
                            SensorManager.PRESSURE_STANDARD_ATMOSPHERE

                    }

                    //invalidating previous measured altitude, so we don't get huge altitude diff changes
                    prevMeasuredAltitude = null

                    isUpdatingBasePressure = false
                }
                catch (e: Exception) {
                    isUpdatingBasePressure = false
                    e.message?.let { log(it) }
                }
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (isUpdatingBasePressure) {
            log("Updating base pressure. Dropping current barometer event")
            return
        }

        p0?.values?.let { values ->
            val currentAltitude = getAltitudeFromPressure(values[0])
            log("barometer reading: $currentAltitude hPa")

            onAltitudeChanged?.let { it(values[0], currentAltitude) }
            prevMeasuredAltitude?.let {
                val altitudeDif = currentAltitude - it

                if (altitudeDif > FLOOR_METERS_THRESHOLD) {
                    onFloorsClimbed?.let { it(altitudeDif / FLOOR_METERS_THRESHOLD) }
                    prevMeasuredAltitude = currentAltitude
                }
                else if (altitudeDif <= 0)
                    prevMeasuredAltitude = currentAltitude

                maxAltitudeChange = if (altitudeDif > maxAltitudeChange) altitudeDif else maxAltitudeChange
            } ?: run {
                prevMeasuredAltitude = currentAltitude
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    private fun getAltitudeFromPressure(currentPressure: Float): Float = SensorManager.getAltitude(seaLevelPressure, currentPressure)

    /*
        alt = GPS altitude
        p = barometer reading (expressed in hPa)

        The altitude must be averaged from at least 10 readings of GPS altitude. To make sure things
        are as accurate as possible.
    */
    private fun getSealevelPressure(alt: Float, p: Float): Float {
        return (p / (1 - alt / 44330.0f).toDouble().pow(5.255)).toFloat()
    }

    fun setOnAltitudeChanged(callback: ((Float, Float) -> Unit)) {
        onAltitudeChanged = callback
    }

    fun setOnFloorClimbed(callback: ((Float) -> Unit)) {
        onFloorsClimbed = callback
    }
}