package com.madgeeks.floorcounter.service

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class MyLocationCallback(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationCallback() {
    companion object {
        private const val MIN_UPDATE_INTERVAL = 5000L//1200000L//20 min
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private var onCoordinatesChanged: ((Double?, Double?) -> Unit)? = null

    override fun onLocationResult(result: LocationResult) {
        super.onLocationResult(result)
        onCoordinatesChanged?.let { callback ->
            result.locations.lastOrNull()?.let { location ->
                callback(location.longitude, location.latitude)
            }
        }
    }

    fun setOnCoordinatesChanged(callback: ((Double?, Double?) -> Unit)) {
        client.removeLocationUpdates(this)
        onCoordinatesChanged = callback

        if ( ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
             !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
             !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            onCoordinatesChanged?.let { it(null, null) }
            return
        }

        client.requestLocationUpdates(
            LocationRequest.Builder(MIN_UPDATE_INTERVAL)
                .setMaxUpdateAgeMillis(MIN_UPDATE_INTERVAL)
                .build(),
            this,
            Looper.getMainLooper()
        )
    }
}