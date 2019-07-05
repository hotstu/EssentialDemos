package github.hotstu.demo.smack

import androidx.annotation.WorkerThread
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.filter.AndFilter
import org.jivesoftware.smack.filter.StanzaIdFilter
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.iqregister.packet.Registration
import org.jivesoftware.smackx.offline.OfflineMessageManager
import timber.log.Timber
import java.util.concurrent.Executors

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @since 7/5/19
 * @desc
 */
class Core {
    companion object {
        init {
            if (BuildConfig.DEBUG) {
                Timber.plant(object : Timber.Tree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        println("$tag: $message")
                    }
                })
            }
        }

        val executors = Executors.newCachedThreadPool {
            Thread(it).apply {
                name = "xmpp-worker"
            }
        }


        val conncetionListener = object : ConnectionListener {
            override fun connected(connection: XMPPConnection?) {
                println("connected")
            }

            override fun connectionClosed() {
                println("connectionClosed")
            }

            override fun connectionClosedOnError(e: Exception?) {
                println("connectionClosedOnError")
            }


            override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
                println("authenticated")
            }

        }
    }

    private var xmpptcpConnection: AbstractXMPPConnection? = null


    private fun requireConn(): AbstractXMPPConnection = xmpptcpConnection!!

    /**
     * 打开连接并登陆
     */
    @WorkerThread
    fun connect(
        username: String,
        password: String,
        serverAddr: String,
        serverPort: Int = 5222,
        resource: String = "android"
    ) {
        xmpptcpConnection?.disconnect()
        val configuration = XMPPTCPConnectionConfiguration.builder().apply {
            setUsernameAndPassword(username, password)
            setResource(resource)
            setXmppDomain(serverAddr)
            setPort(serverPort)
            setSendPresence(false)// 状态设为离线，目的为了取离线消息
            if (BuildConfig.DEBUG) {
                enableDefaultDebugger()
            }
        }.build()
        xmpptcpConnection = XMPPTCPConnection(configuration)
        requireConn().connect()
    }

    /**
     * 关闭连接
     */
    @WorkerThread
    fun disconnect() {
        requireConn().disconnect()
        xmpptcpConnection = null
    }

    /**
     * 登录
     *
     * @param account  登录帐号
     * @param password 登录密码
     * @return
     */
    fun login(): Boolean {
        xmpptcpConnection!!.login()

        // 更改在綫狀態
        val presence = Presence(Presence.Type.available)
        with(requireConn()) {
            sendStanza(presence)
            addConnectionListener(conncetionListener)
        }
        return true
    }

    /**
     * 查询服务器支持的注册参数
     */
    fun querySignUpProp() {
        val reg = Registration().apply {
            type = IQ.Type.get
        }
        val filter = AndFilter(
            StanzaIdFilter(
                reg.stanzaId
            ), StanzaTypeFilter(IQ::class.java)
        )
        val collector = requireConn().createStanzaCollector(
            filter
        )
        requireConn().sendStanza(reg)
        val result = collector.nextResult() as IQ
        // Stop queuing results停止请求results（是否成功的结果）
        collector.cancel()
        Timber.d("result: $result")
    }

    /**
     * 注册
     *
     * @param account  注册帐号
     * @param password 注册密码
     * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
     */
    fun signUp(account: String, password: String): String {
        val reg = Registration()
        reg.type = IQ.Type.set
        // 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。

        val filter = AndFilter(
            StanzaIdFilter(
                reg.stanzaId
            ), StanzaTypeFilter(IQ::class.java)
        )
        val collector = requireConn().createStanzaCollector(
            filter
        )
        requireConn().sendStanza(reg)
        val result = collector.nextResult() as IQ
        // Stop queuing results停止请求results（是否成功的结果）
        collector.cancel()
        Timber.d("result: $result")
        if (result.type == IQ.Type.result) {
            Timber.v("regist success.")
            return "1"
        } else { // if (result.getType() == IQ.Type.ERROR)
            if (result.error.toString().equals("conflict(409)", ignoreCase = true)) {
                Timber.e("IQ.Type.ERROR: ${result.error}" )
                return "2"
            } else {
                Timber.e( "IQ.Type.ERROR: ${result.error}" )
                return "3"
            }
        }
    }


    fun getOfflineMessages() {
        val offlineMessageManager = OfflineMessageManager(xmpptcpConnection)
        offlineMessageManager.messages.map {
            Timber.d("<<<offlineMsg: $it")
        }
    }

    fun getUserProfile(name: String? = null) {
        //TODO
    }

    fun sendTextMsg(target: String) {
        //TODO
    }


}