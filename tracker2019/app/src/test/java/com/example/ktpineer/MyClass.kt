package com.example.ktpineer

public fun say() {

}
/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/8/19
 * @desc
 */
class MyClass {
    var name = "hello,world"
    lateinit var nut:String
    lateinit var callback: (String)->Unit
    public  val item  = listOf("a", "b")


    fun init() {
        nut = "xxxx"
    }

    fun setCall(callback: (String)->Unit) {
        this.callback = callback
    }

    fun makeCallback(str: String) = callback(str)

}