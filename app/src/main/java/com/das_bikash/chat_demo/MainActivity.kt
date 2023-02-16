package com.das_bikash.chat_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.das_bikash.chat_demo.utils.debugLog
import com.das_bikash.chat_demo.utils.runOnMainThread
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        private const val URL = "http://192.168.0.130:3000"
    }

    private val mSocket: LifeCycleAwareSocket by lazy {
        LifeCycleAwareSocket.getInstance(URL,this)
    }

    private val logMessageAdapter by lazy { LogMessageAdapter() }

    private val logMessages = mutableListOf<LogMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvLogMessage.adapter = logMessageAdapter

        btnStart.setOnClickListener { start() }

        btnStop.setOnClickListener {
            mSocket.close()
        }

        ivSendMessage.setOnClickListener {
            etMessage.text?.toString()?.let { message ->
                if (message.isNotBlank()){
                    mSocket.let { socket ->
                        socket.emit("echo", message)
                        displayReceived(message)
                    }
                }
            }
            etMessage.setText("")
        }

        updateUi(mSocket.isConnected.debugLog())

    }

    private fun start() {
        mSocket.connect(
            onConnect = {
                updateUi(true)
                it?.let { socket ->
                    socket.on("echo"
                    ) { args ->
                        displayReceived(args?.joinToString(separator = " | ") { it1 ->
                            it1.toString()
                        }.orEmpty())
                    }
                    ivSendMessage.setOnClickListener{
                        etMessage.text?.toString()?.apply {
                            socket.emit("echo",this)
                            etMessage.setText("")
                        }.orEmpty()
                    }
                }
            },
            onDisConnect = {
                updateUi(false)
                ivSendMessage.setOnClickListener(null)
            }
        )

    }

    private fun updateUi(open:Boolean) {
        runOnMainThread {
            btnStart.isEnabled = open.not()
            btnStop.isEnabled = open
            etMessage.isEnabled = open
            ivSendMessage.isEnabled = open
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

