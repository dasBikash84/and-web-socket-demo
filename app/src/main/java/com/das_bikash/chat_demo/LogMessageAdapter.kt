package com.das_bikash.chat_demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.das_bikash.chat_demo.R
import kotlinx.android.synthetic.main.layout_log_message.view.*

class LogMessageAdapter()
    : ListAdapter<LogMessage, LogMessageAdapter.Companion.LogMessageViewHolder>(
    getLogMessageDiffUtils()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogMessageViewHolder {
        return LogMessageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_log_message, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LogMessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object{

        class LogMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(logMessage: LogMessage){
                itemView.tvMessage.text = logMessage.displayMessage
                itemView.tvMessage.setTextColor(logMessage.displayColor)
            }
        }

        private fun getLogMessageDiffUtils() = object : DiffUtil.ItemCallback<LogMessage>(){
            override fun areItemsTheSame(oldItem: LogMessage, newItem: LogMessage): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: LogMessage, newItem: LogMessage): Boolean =
                oldItem == newItem
        }
    }
}