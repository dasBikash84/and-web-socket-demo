package com.das_bikash.chat_demo.utils

import android.os.Handler
import android.os.Looper


fun <T> runOnMainThread(delayMs: Long = 0L, task: () -> T) {
    Handler(Looper.getMainLooper()).postDelayed({
        try{ task.invoke() }catch (ex:Throwable){ex.printStackTrace()}
    }, delayMs)
}


fun <T> T.debugLog():T = this.apply {
    println("chat_demo log: $this")
}