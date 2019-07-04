package com.example.tracker2019.location

import android.annotation.SuppressLint
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.tracker2019.App
import com.example.tracker2019.Constants
import com.example.tracker2019.R
import com.example.tracker2019.notification.Util
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/13/19
 * @desc
 */
object AMapClient {
    val callbacks: ArrayList<AMapLocationListener> = ArrayList()
    @SuppressLint("StaticFieldLeak")
    val client: AMapLocationClient = AMapLocationClient(App.sInstance)
    val permissionPrompt: AtomicBoolean = AtomicBoolean(true)

    init {
        client.setLocationOption(
            AMapLocationClientOption()
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setGpsFirst(true)
                .setGpsFirstTimeout(30000)
                .setInterval(2000)
        )
        client.setLocationListener { aMapLocation ->

            if (aMapLocation.errorCode == AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION && permissionPrompt.get()) {
                permissionPrompt.set(false)
                val notification = Util.create(App.sInstance, Constants.CH2) {
                    setSmallIcon(R.mipmap.ic_launcher)
                    setContentTitle("没有定位权限")
                    setContentText("请点击授予定位权限")
                }
                Util.show(App.sInstance, 1, notification)
            }

            if (aMapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
                for (callback in callbacks) {
                    callback.onLocationChanged(aMapLocation)
                }
            }

        }
    }

    fun addCallback(callback: AMapLocationListener) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
        if (callbacks.size > 0) {
            client.startLocation()
        }
    }

    fun removeCallback(callback: AMapLocationListener) {
        callbacks.remove(callback)
        if (callbacks.size == 0) {
            client.stopLocation()
        }
    }


}