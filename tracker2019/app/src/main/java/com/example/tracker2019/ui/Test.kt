package com.example.tracker2019.ui

import android.content.Context
import android.content.Intent

import com.example.tracker2019.MainActivity

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @desc
 * @since 5/14/19
 */
class Test {
    fun startActivity(ctx: Context) {
        val i = Intent(ctx, MainActivity::class.java)
        ctx.startActivity(i)
    }
}
