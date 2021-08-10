package com.example.web_socket_example

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class MainActivity : AppCompatActivity() {

    private val client: OkHttpClient by lazy { OkHttpClient() }
    private var ws: WebSocket?=null

    private val logMessageAdapter by lazy { LogMessageAdapter() }

    private val logMessages = mutableListOf<LogMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvLogMessage.adapter = logMessageAdapter

        btnStart.setOnClickListener { start() }

        btnStop.setOnClickListener {
            ws?.close(EchoWebSocketListener.NORMAL_CLOSURE_STATUS, "Goodbye !")
            ws = null
        }

        ivSendMessage.setOnClickListener {
            etMessage.text?.toString()?.let { message ->
                if (message.isNotBlank()){
                    ws?.let { socket ->
                        socket.send(message)
                        displaySend(message)
                    }
                }
            }
            etMessage.setText("")
        }

    }

    private fun start() {
        val request: Request = Request.Builder().url("ws://echo.websocket.org").build()
        println(request.url().toString())
        val listener = EchoWebSocketListener(
            ::displayReceived,
            ::displayError,
            ::displayStatusMessage,
            ::onConOpen,
            ::onConClose
        )
        ws = client.newWebSocket(request, listener)

    }

    private fun onConClose() {
        runOnMainThread {
            btnStart.isEnabled = true
            btnStop.isEnabled = false
            ws = null
        }
    }

    private fun onConOpen() {
        runOnMainThread {
            btnStart.isEnabled = false
            btnStop.isEnabled = true
        }
    }

    private fun displayMessage(message: String,displayTextColor:Int=Color.BLACK) {
        runOnMainThread {
            val logMessage = LogMessage(message,displayTextColor)
            logMessages.add(logMessage)
            logMessageAdapter.submitList(
                logMessages.sortedByDescending { it.timeStamp }
            )
            runOnMainThread (500L) {
                rvLogMessage.scrollToPosition(0)
            }
        }
    }

    private fun displaySend(message: String)
        = displayMessage(getString(R.string.sent_str,message),Color.WHITE)

    private fun displayReceived(message: String)
        = displayMessage(getString(R.string.received_str,message),Color.MAGENTA)

    private fun displayError(message: String)
        = displayMessage(getString(R.string.error_str,message),Color.RED)

    private fun displayStatusMessage(message: String) = displayMessage(getString(R.string.status_str,message))
}

