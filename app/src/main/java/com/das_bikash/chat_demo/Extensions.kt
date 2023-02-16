package com.das_bikash.chat_demo

import android.os.Handler
import android.os.Looper


fun <T> runOnMainThread(delayMs: Long = 0L, task: () -> T) {
    Handler(Looper.getMainLooper()).postDelayed({
        try{ task.invoke() }catch (ex:Throwable){ex.printStackTrace()}
    }, delayMs)
}
