package com.example.homeworkfragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.homeworkfragments.R
import com.example.homeworkfragments.database.TranslatedMessage
import com.example.homeworkfragments.diffUtil.EnglishDiffCallback
import kotlinx.android.synthetic.main.item_msg_received.view.*
import kotlinx.android.synthetic.main.item_msg_send.view.*

private const val VIEW_TYPE_MESSAGE_SENT = 1
private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
private var messageList = mutableListOf<TranslatedMessage>()

class EnglishChatAdapter(
    private val clickListener: (message: TranslatedMessage, itemView: View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val diffCallback =
        EnglishDiffCallback()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            SendMsgViewHolder(
                inflater,
                parent
            )
        } else {
            ReceivedMsgViewHolder(
                inflater,
                parent
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        var message: String = messageList[position].type
        if (message == "sent") {
            return VIEW_TYPE_MESSAGE_SENT
        } else if (message == "received") {
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
        return 0
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        var message: TranslatedMessage = messageList[position]
        super.onBindViewHolder(holder, position, payloads)
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SendMsgViewHolder).bind(message, clickListener)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMsgViewHolder).bind(message, clickListener)
        }
    }

    fun setMessages(list: List<TranslatedMessage>) {
        diffCallback.setItems(messageList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback, false)
        messageList.clear()
        messageList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    private class SendMsgViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.item_msg_send, parent, false)){
        private val msgTextView = itemView.msgTextView
        fun bind(message: TranslatedMessage, clickListener: (message: TranslatedMessage, itemView: View) -> Unit) {
            msgTextView.text = message.text
            msgTextView.setOnClickListener {
                clickListener(message, msgTextView)
            }
        }
    }

    private class ReceivedMsgViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.item_msg_received, parent, false)){
        private val msgTextView = itemView.tMsgTextView
        fun bind(message: TranslatedMessage, clickListener: (message: TranslatedMessage, itemView: View) -> Unit){
            msgTextView.text = message.text
            msgTextView.setOnClickListener {
                clickListener(message, msgTextView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var message: TranslatedMessage = messageList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SendMsgViewHolder).bind(message, clickListener )
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMsgViewHolder).bind(message, clickListener)
        }
    }
}