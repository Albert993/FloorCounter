package com.madgeeks.floorcounter.Utils

class Constants {
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_TIME_FORMAT_HOUR = "yyyy-MM-dd'T'HH':00'"
        const val WEATHER_API_BASE_URL = "https://api.open-meteo.com/v1/"
        const val ROOM_DATABASE_NAME = "FloorCountingDb"

        // Intent Constants
        const val inputExtra = "inputExtra"
        const val activityRecognitionRequestCode = 10001

        // Notification Constants
        const val channelID = "floorsTestChannelId"
        const val foregroundServiceNotificationTitle = "Floors Counter Foreground Services"
        const val foregroundIntentServiceNotificationTitle = "Floors Counter Intent Service"
        const val notificationChannelName = "Floors Counter Channel"

        // Job scheduler Constants
        const val jobId = 123
    }

}