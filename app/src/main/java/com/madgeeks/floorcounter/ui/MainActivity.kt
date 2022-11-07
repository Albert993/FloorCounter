package com.madgeeks.floorcounter.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.madgeeks.floorcounter.Utils.Constants
import com.madgeeks.floorcounter.R
import com.madgeeks.floorcounter.Utils.showMissingPermissionsSnackbar
import com.madgeeks.floorcounter.service.ForegroundService
import com.madgeeks.floorcounter.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private val permissions = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val iterator = permissions.entries.iterator()
            while (iterator.hasNext()) {
                val elem = iterator.next()
                if (!elem.value) {
                    showMissingPermissionsSnackbar()
                    return@registerForActivityResult
                }
            }

            startService()
        }

        requestNotificationPermissionLauncher.launch(permissions)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

    private fun startService() {
        val myServiceIntent = Intent(this, ForegroundService::class.java)
        myServiceIntent.putExtra(Constants.inputExtra, "testshit")
        ContextCompat.startForegroundService(this, myServiceIntent)
    }
}