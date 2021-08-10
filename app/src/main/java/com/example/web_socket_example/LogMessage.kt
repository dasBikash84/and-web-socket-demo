package com.example.web_socket_example

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

data class LogMessage(
    val message:String,
    val displayColor:Int = Color.BLACK,
){
    val timeStamp:Long = System.currentTimeMillis()

    val displayMessage:String
        get() = "${dateFormatter.format(Date(timeStamp))} | $message"

    companion object{
        private const val displayDateStringFormat = "EEE, d MMM yyyy HH:mm:ss.SSS Z"
        private val dateFormatter by lazy {
            SimpleDateFormat(displayDateStringFormat, Locale.getDefault())
        }
    }
}