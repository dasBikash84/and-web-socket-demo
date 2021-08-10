package com.example.web_socket_example

import android.os.Handler
import android.os.Looper


fun <T> runOnMainThread(delayMs: Long = 0L, task: () -> T) {
    Handler(Looper.getMainLooper()).postDelayed({
        try{ task.invoke() }catch (ex:Throwable){ex.printStackTrace()}
    }, delayMs)
}
