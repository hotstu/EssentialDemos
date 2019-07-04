package com.example.tracker2019

import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/21/19
 * @desc
 */
inline fun doAfterBuildVersion(version: Int, inclusive: Boolean = true, func: () -> Unit) {
    if (inclusive) {
        if (Build.VERSION.SDK_INT >= version) {
            func()
        }
    }
    if (!inclusive) {
        if (Build.VERSION.SDK_INT > version) {
            func()
        }
    }
}

inline fun <reified T> Context.getSystemServicex(): T? =
    ContextCompat.getSystemService(this, T::class.java)