package com.das_bikash.chat_demo

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.das_bikash.chat_demo.utils.debugLog
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class LifeCycleAwareSocket private constructor(
    url:String, lifecycleOwner: LifecycleOwner
): DefaultLifecycleObserver {

    companion object{
        fun getInstance(
            url: String,lifecycleOwner: LifecycleOwner
        ) = LifeCycleAwareSocket(url, lifecycleOwner)
    }

    private val socket: Socket?

    init {
        socket = initSocket(url)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private fun initSocket(url: String):Socket? {
        return socket?:  try {
            IO.socket(url)
        } catch (e: Throwable) {
            null
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        "onDestroy".debugLog()
        socket?.run {
            close()
            off()
        }
    }

    fun on(event: String, fn: Emitter.Listener): Emitter?{
        return socket?.apply {  on(event, fn) }
    }

    fun once(event: String, fn: Emitter.Listener): Emitter?{
        return socket?.apply {  once(event, fn) }
    }

    fun off() {
        socket?.off()
    }

    fun off(event: String): Emitter? {
        return socket?.off(event)
    }

    fun off(event: String, fn: Emitter.Listener): Emitter? {
        return socket?.apply {  off(event, fn) }
    }

    fun emit(event:String, vararg elements: Any){
        emit(event, arrayOf(elements),null)
    }

    fun emit(event:String, args:Array<Any>?, ack:Ack?=null){
        socket?.emit(event, args, ack)
    }

    fun close(){
        socket?.close()
    }

    fun connect(
        onConnect: ((socket: Socket?)->Unit)?=null,
        onDisConnect: (()->Unit)?=null
    ) =  socket?.connect()?.run {
        on(Socket.EVENT_CONNECT) {
            onConnect?.invoke(socket)
        }
        on(Socket.EVENT_DISCONNECT){
            onDisConnect?.invoke()
        }
    }

    fun disconnect() = socket?.connect()

    val isConnected get() = socket?.connected() == true
}
