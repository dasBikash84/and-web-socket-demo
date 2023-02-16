package com.das_bikash.chat_demo

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class EchoWebSocketListener(
    private val displayReceived: (String) -> Unit,
    private val displayError: (String) -> Unit,
    private val displayStatusMessage: (String) -> Unit,
    private val onOpen:()->Unit,
    private val onClose:() -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response?) {
        onOpen.invoke()
        displayStatusMessage("Connection opened......")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Received string : $text")
        displayReceived(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Received bytes : " + bytes.hex())
        displayReceived(bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Closing : $code / $reason")
        displayStatusMessage("Connection closed : Code:$code, Reason: $reason")
        onClose.invoke()
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        println("Error : " + t.message)
        displayError(t.message ?: "Unknown error!!")
        onClose.invoke()
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}