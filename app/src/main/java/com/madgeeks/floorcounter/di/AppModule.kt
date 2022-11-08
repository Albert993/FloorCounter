package com.madgeeks.floorcounter.di

import androidx.room.Room
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.LocationServices
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.data.db.FloorCounterDb
import com.madgeeks.floorcounter.data.db.MainViewModelRepository
import com.madgeeks.floorcounter.data.db.MainViewModelRepositoryImpl
import com.madgeeks.floorcounter.data.remote.WeatherAPI
import com.madgeeks.floorcounter.receiver.ActivityTransitionReceiver
import com.madgeeks.floorcounter.service.MyLocationCallback
import com.madgeeks.floorcounter.service.Notifications
import com.madgeeks.floorcounter.service.PressureSensorListener
import com.madgeeks.floorcounter.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { Notifications(androidContext()) }
    single { PressureSensorListener(androidContext(), get(), get()) }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }
    single { MyLocationCallback(androidContext(), get()) }
    single { ActivityRecognition.getClient(androidContext()) }
    single { ActivityTransitionReceiver() }

    single {
        Room.databaseBuilder(
            androidContext(),
            FloorCounterDb::class.java,
            Constants.ROOM_DATABASE_NAME
        ).build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(Constants.WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(WeatherAPI::class.java)
    }
    single<MainViewModelRepository> { MainViewModelRepositoryImpl(get()) }
    single{
        val database = get<FloorCounterDb>()
        database.hourDao()
    }
}

val viewModelModules = module {
    viewModel { MainViewModel(get()) }
}