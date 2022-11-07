package com.madgeeks.floorcounter.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherAPI {
    @GET("forecast/")
    suspend fun getWeather(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("hourly") weatherParams: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<WeatherDto>
}