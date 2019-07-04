package com.example.tracker2019

import android.app.AlarmManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.transaction
import github.hotstu.naiue.arch.MOFragmentActivity

class MainActivity : MOFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.transaction {
            replace(R.id.frame, MyFragment())
        }
        doAfterBuildVersion(Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
        val systemServicex = getSystemServicex<AlarmManager>()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


