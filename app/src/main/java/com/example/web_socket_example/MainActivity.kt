package com.example.web_socket_example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okio.ByteString
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private val displayDateStringFormat = "EEE, d MMM yyyy HH:mm:ss.SSS Z"
    private val dateFormatter by lazy {
        SimpleDateFormat(displayDateStringFormat, Locale.getDefault())
    }

    private val client: OkHttpClient by lazy { OkHttpClient() }
    private var ws: WebSocket?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            ::displaySend,
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
        }
    }

    private fun onConOpen() {
        runOnMainThread {
            tvSendMessage.text = ""
            tvErrorMessage.text = ""
            tvErrorMessage.text = ""
            btnStart.isEnabled = false
            btnStop.isEnabled = true
        }
    }

    private fun displaySend(message: String) {
        runOnMainThread {
            tvSendMessage.text = "${getTimeStamp()} | Sent : $message"
            tvReceivedMessage.text = ""
            tvErrorMessage.text = ""
        }
    }

    private fun getTimeStamp() = dateFormatter.format(Date())

    private fun displayReceived(message: String) {
        runOnMainThread{
            tvReceivedMessage.text = "${getTimeStamp()} | Received: $message"
            tvErrorMessage.text = ""
        }
    }

    private fun displayError(message: String) {
        runOnMainThread {
            tvReceivedMessage.text = ""
            tvErrorMessage.text = "${getTimeStamp()} | Error: $message"
        }
    }

    private fun displayStatusMessage(message: String) {
        runOnMainThread {
            tvStatus.text = "${getTimeStamp()} | Status: $message"
        }
    }

    private class EchoWebSocketListener(
        val displaySend: (String) -> Unit,
        val displayReceived: (String) -> Unit,
        val displayError: (String) -> Unit,
        val displayStatusMessage: (String) -> Unit,
        val onOpen:()->Unit,
        val onClose:() -> Unit
    ) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response?) {
            displayStatusMessage("Connection opened......")
            onOpen.invoke()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("Received string : $text")
            displayStatusMessage("Received string : $text")
            displayReceived(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Received bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            println("Closing : $code / $reason")
            displayStatusMessage("Connection closed : Code:$code, Reason: $reason")
            onClose.invoke()
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
            println("Error : " + t.message)
            displayStatusMessage("Error : " + t.message)
            displayError("Error : " + t.message)
            onClose.invoke()
        }

        companion object {
            const val NORMAL_CLOSURE_STATUS = 1000
        }
    }
}

fun <T> runOnMainThread(delayMs: Long = 0L, task: () -> T) {
    Handler(Looper.getMainLooper()).postDelayed({
        try{ task.invoke() }catch (ex:Throwable){ex.printStackTrace()}
    }, delayMs)
}