package com.das_bikash.chat_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.das_bikash.chat_demo.R
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object{
        private const val URL = "http://192.168.0.130:3000"
    }

    private val mSocket: Socket? by lazy {
        try {
            IO.socket(URL)
        } catch (e:Throwable) {
            null
        }
    }

    private val logMessageAdapter by lazy { LogMessageAdapter() }

    private val logMessages = mutableListOf<LogMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvLogMessage.adapter = logMessageAdapter

        btnStart.setOnClickListener { start() }

        btnStop.setOnClickListener {
            mSocket?.close()

            btnStop.isEnabled = false
            btnStart.isEnabled = true
        }

        ivSendMessage.setOnClickListener {
            etMessage.text?.toString()?.let { message ->
                if (message.isNotBlank()){
                    mSocket?.let { socket ->
                        socket.emit("echo",message)
                        displaySend(message)
                    }
                }
            }
            etMessage.setText("")
        }

    }

    private fun start() {
//        val request: Request = Request.Builder().url(URL).build()
//        println(request.url().toString())
//        val listener = EchoWebSocketListener(
//            ::displayReceived,
//            ::displayError,
//            ::displayStatusMessage,
//            ::onConOpen,
//            ::onConClose
//        )
//        ws = client.newWebSocket(request, listener)
        mSocket?.open()
        btnStop.isEnabled = true
        btnStart.isEnabled = false
        mSocket?.on("echo",object : Emitter.Listener{
            override fun call(vararg args: Any?) {
               displayMessage(args?.map { it.toString() }?.joinToString(separator = " | "))
            }

        })
    }

    private fun onConClose() {
        runOnMainThread {
            btnStart.isEnabled = true
            btnStop.isEnabled = false
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

