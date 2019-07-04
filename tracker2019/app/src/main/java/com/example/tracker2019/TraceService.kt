package com.example.tracker2019

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.example.tracker2019.db.Trace
import com.example.tracker2019.db.TraceDao
import com.example.tracker2019.db.TraceEntity
import com.example.tracker2019.location.AMapClient
import com.example.tracker2019.notification.Util
import timber.log.Timber

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/13/19
 * @desc
 */
class TraceService: Service(), AMapLocationListener{
    companion object {
        const val ACTION_START = "com.example.tracker2019.loc.start"
        const val ACTION_STOP = "com.example.tracker2019.loc.stop"
    }
    lateinit var traceDao: TraceDao
    override fun onLocationChanged(p0: AMapLocation?) {
        Timber.d("onLocationChanged ${p0?.latitude}, ${p0?.longitude}")
        traceDao.add(TraceEntity.from(p0!!))
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return  START_REDELIVER_INTENT
        }
        if (intent.action == ACTION_START) {
            val id = intent.getStringExtra("id")
            traceDao = Trace.database(id).traceDao()
            AMapClient.addCallback(this)
            val notification = Util.create(this,
                Constants.CH2
            ) {
                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle("轨迹采集中")
                setContentText("请不要强行退出")
                setAutoCancel(false)
            }
            //Util.show(this, 0, notification)
            startForeground(99, notification)
            Trace.traceState.value = true

        } else if (intent.action == ACTION_STOP) {
            Trace.traceState.value = false
            stopForeground(true)
            stopSelf()
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        AMapClient.removeCallback(this)
        Trace.traceState.value = false
        super.onDestroy()
    }
}