package com.example.tracker2019

import android.app.Activity
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary
import github.hotstu.labo.tool.*
import timber.log.Timber


/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/13/19
 * @desc
 */
class App : Application(), Application.ActivityLifecycleCallbacks {
    companion object {
        lateinit var sInstance: App
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            );
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )

            if (!LeakCanary.isInAnalyzerProcess(this)) {
                LeakCanary.install(this);
            }
        }
        registerActivityLifecycleCallbacks(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val config = config {
                group(groupId = "chatGroup", groupName = "chat") {
                    channel(id = Constants.CH1) {
                        name = "channel1"
                        description = "when some chat request come"
                    }
                    channel(id = Constants.CH2) {
                        name = "channel2"
                        description = "when some important chat request come"
                        importance = NotificationManager.IMPORTANCE_HIGH
                    }

                }
                group(groupId = "noticeGroup", groupName = "notice") {
                    channel(id = Constants.CH3) {
                        name = "personal message"
                        description = "send personal message"
                    }
                    channel(id = Constants.CH4) {
                        name = "broadcast message"
                        description = "send broadcast"
                    }
                }
            }
            config.applyTo(notificationManager)
            config.clean()
        }
    }


    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        Timber.d("${activity!!.javaClass.simpleName} onCreated")
    }

    override fun onActivityStarted(activity: Activity?) {
    }

}