package com.example.ktpineer

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/22/19
 * @desc
 */
class MyGeneric<T>(var name: T, val other: String) {
    fun tell() {
        println("name = ${name},${other}")
    }
}