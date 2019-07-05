package github.hotstu.demo.smack

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class UnLoginTest {

    lateinit var core: Core

    @Before
    fun init() {
        core = Core()
        core.connect("hbot", "hbot", "616.pub")
        //core.login()
    }

    @Test
    fun testQuerySignUpProp() {
        core.querySignUpProp()
    }

    @Test
    fun testOfflineMsg() {

        core.getOfflineMessages()

        //Thread.sleep(10000)
        //xmpptcpConnection?.disconnect()
        //println("disconnect done")
        //executorService.shutdown()
    }

    @After
    fun terminate() {
        core.disconnect()
    }
}