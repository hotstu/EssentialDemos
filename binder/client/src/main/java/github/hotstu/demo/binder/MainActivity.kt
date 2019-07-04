package github.hotstu.demo.binder

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    companion object {
        init {
            Timber.plant(object : Timber.Tree() {
                @SuppressLint("LogNotTimber")
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Log.d(tag ?: "EchoServer", "${Thread.currentThread().name}>>> $message")
                }
            })
        }
    }

    var iEchoServer: IEchoServer? = null
    private val eventHandler = object : ICallback.Stub() {
        override fun onEvent(aString: String?) {
            Timber.d("onEvent:${aString}")
        }
    }
    private val eventHandler2 = object : ICallback.Stub() {
        override fun onEvent(aString: String?) {
            Timber.d("onEvent:${aString}")
            //throw RuntimeException()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.e("onServiceDisconnected:$name")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.e("onServiceConnected:$name")
            service?.linkToDeath({
                Timber.e("$service:dead")
            }, 0)
            iEchoServer = IEchoServer.Stub.asInterface(service)
            iEchoServer?.registerCallback(eventHandler)
        }

        override fun onBindingDied(name: ComponentName?) {
            Timber.e("onBindingDied:$name")
            //TODO rebind in main Thread with delay?
            //TODO need a local proxy to record registered callbacks and register again
            unbindService(this)

            bindService()
        }

        override fun onNullBinding(name: ComponentName?) {
            Timber.e("onNullBinding:$name")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( "github.hotstu.demo.binder.BIND") == PERMISSION_GRANTED) {
            } else {
                requestPermissions(arrayOf("github.hotstu.demo.binder.BIND"), 0)
            }
        } else {
            bindService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0 && grantResults.none { it != PERMISSION_GRANTED }) {
            bindService()
        }
    }

    private fun bindService() {
        val intent = Intent()
        intent.component = ComponentName(
                "github.hotstu.demo.binder.serverapp",
                "github.hotstu.demo.binder.EchoServer"
        )

        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    fun onClick(v: View) {
        println("${android.os.Process.myPid()}: send echo request")
        Timber.d(iEchoServer?.sendSignal("hello, world"))
    }

    fun onClick2(v: View) {
        Timber.d("client: register callback ${eventHandler}")
        iEchoServer?.registerCallback(eventHandler)

    }

    fun onClick3(v: View) {
        Timber.d("client: register callback ${eventHandler}")
        iEchoServer?.registerCallback(eventHandler2)

    }

    fun unregister1(v: View) {
        Timber.d("client: unregister callback ${eventHandler}")
        iEchoServer?.unRegisterCallback(eventHandler)
    }

    fun unregister2(v: View) {
        Timber.d("client: unregister callback ${eventHandler2}")
        iEchoServer?.unRegisterCallback(eventHandler2)
    }


}
