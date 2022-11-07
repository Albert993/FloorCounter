package com.madgeeks.floorcounter.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.data.db.Hour
import com.madgeeks.floorcounter.data.db.MainViewModelRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(private val repository: MainViewModelRepository) : ViewModel() {
    private var hoursLiveData: LiveData<List<Hour>> = repository.getAllHours()
    private var hoursObserver: Observer<List<Hour>>? = null

    var floors = MutableLiveData<Float>()
    private set

    init {
        hoursObserver = Observer<List<Hour>>{ hourList ->
            floors.postValue(
                hourList
                    .filter{ hour -> hour.date.contains(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)))}
                    .sumOf { hour -> hour.floors.toDouble() }.toFloat()
            )
        }

        hoursObserver?.let { hoursLiveData.observeForever(it) }
    }

    override fun onCleared()
    {
        hoursObserver?.let { hoursLiveData.removeObserver(it) }
        super.onCleared()
    }
}