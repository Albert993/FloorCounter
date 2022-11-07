package com.madgeeks.floorcounter.data.remote

import com.madgeeks.floorcounter.Utils.Constants
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class WeatherDto (
    val latitude: String,
    val longitude: String,
    val timezone: String,
    val hourly: TimeDto
) {
    fun getSeaLevelPressure(): Float? {
        val currentHourString = LocalDateTime.now(ZoneId.of(timezone)).format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT_HOUR))
        hourly.time.indexOf(currentHourString).apply {
            return if (this == -1) null else hourly.pressure_msl[this]
        }
    }
}