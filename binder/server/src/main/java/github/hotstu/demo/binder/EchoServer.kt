package github.hotstu.demo.binder

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.GuardedBy
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @since 7/3/19
 * @desc
 */
class EchoServer : Service() {
    companion object {
        init {
            Timber.plant(object : Timber.Tree() {
                @SuppressLint("LogNotTimber")
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Log.d(tag?:"EchoServer", "${Thread.currentThread().name}>>> $message")
                }
            })
        }
        data class Listener(val token: IBinder, val receipant: IBinder.DeathRecipient) {
            init {
                token.linkToDeath(receipant, 0)
            }
        }

        val accessLock: Any = Any()


        class Binder(val service: EchoServer) : IEchoServer.Stub() {

            @GuardedBy("accessLock")
            @Volatile
            var callbacks: ArrayList<Listener> = ArrayList()

            override fun registerCallback(callback: ICallback) {
                val token = callback.asBinder()
                Timber.d("${token} connected")

                synchronized(accessLock) {
                    if (callbacks.firstOrNull { it.token == token } == null) {

                        val listener = Listener(token, IBinder.DeathRecipient {
                            Timber.e( "${token} is dead, remove ....")
                            unRegisterCallback(token)
                        })
                        callbacks.add(listener)
                    }
                }
                updateState()
            }

            override fun unRegisterCallback(callback: ICallback) {
                unRegisterCallback(callback.asBinder())
            }

            private fun unRegisterCallback(token: IBinder) {
                Timber.d( "unRegisterCallback")
                synchronized(accessLock) {
                    callbacks.filter { it.token == token }.forEach {
                        callbacks.remove(it)
                        it.token.unlinkToDeath(it.receipant, 0)
                    }
                }
                updateState()
            }

            override fun sendSignal(str: String): String {
                return "${android.os.Process.myPid()}: ok"
            }

            private fun updateState() {
                val isempty = synchronized(accessLock) {
                    callbacks.isEmpty()
                }
                if (isempty) {
                    service.stopLooping()
                } else {
                    service.startLooping()
                }
            }

            fun senEvent(event: String) {
                Timber.d( "senEvent")
                val unmodifiableList = synchronized(accessLock) {
                    Collections.unmodifiableList(callbacks)
                }
                unmodifiableList.forEachIndexed { index, it ->
                    try {
                        if (it.token.isBinderAlive) {
                            ICallback.Stub.asInterface(it.token).onEvent(">>>>>>$index: $event")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    val mbinder: Binder by lazy {
        Binder(this)
    }
    val handler = Handler()

    fun startLooping() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(object : Runnable {
            override fun run() {
                mbinder.senEvent("event")
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    fun stopLooping() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        stopLooping()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Timber.d("onBind")
        return mbinder
    }
}