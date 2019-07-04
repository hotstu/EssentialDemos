package com.example.ktpineer

import org.jetbrains.annotations.NotNull

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/13/19
 * @desc
 */
class KO(name: @NotNull String) : JO(name) {
    init {

        println("init KO with $name")
    }
}