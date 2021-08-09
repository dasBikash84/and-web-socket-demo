package com.example.web_socket_example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okio.ByteString
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        client = OkHttpClient()

        btnStart.setOnClickListener { start() }
        btnSend.setOnClickListener { Date().toString().let {
            println("Sending: ${it}")
            ws.send(it)
        } }

        btnStop.setOnClickListener {
            ws.close(EchoWebSocketListener.NORMAL_CLOSURE_STATUS, "Goodbye !")
        }
    }

    private fun start() {
        val request: Request = Request.Builder().url("ws://echo.websocket.org").build()
        println(request.url().toString())
        val listener = EchoWebSocketListener()
        ws = client.newWebSocket(request, listener)

    }

    private class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response?) {
            println("Sending: Hello, it's SSaurel !")
            webSocket.send("Hello, it's SSaurel !")
//            webSocket.send("What's up ?")
//            println("Sending:What's up ?")
//            webSocket.send(ByteString.decodeHex("deadbeef"))
//            println("Sending:${ByteString.decodeHex("deadbeef")}")
//            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Receiving : $text")

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            println("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
            println("Error : " + t.message)
        }

        companion object {
            const val NORMAL_CLOSURE_STATUS = 1000
        }
    }

    private fun output(message:String){

    }
}
