package com.example.homeworkfragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homeworkfragments.R
import com.example.homeworkfragments.database.LikedMessage
import kotlinx.android.synthetic.main.item_liked.view.*

val likedMessages = mutableListOf<LikedMessage>()
class LikedAdapter(
    private val deleteListener: (item: LikedMessage) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LikedViewHolder(
            inflater,
            parent
        )
    }

    override fun getItemCount(): Int = likedMessages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LikedViewHolder).bind(position, deleteListener)
    }

    fun setItems(list: MutableList<LikedMessage>) {
        likedMessages.clear()
        likedMessages.addAll(list)
        notifyDataSetChanged()
    }

    private class LikedViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.item_liked, parent, false)) {
        private val orgTextView = itemView.likedOrgTextView
        private val transTextView = itemView.likedTransTextView
        private val deleteLikedButton = itemView.deleteLikedButton

        fun bind (position: Int, deleteListener: (item: LikedMessage) -> Unit ){
            orgTextView.text = likedMessages[position].text
            transTextView.text = likedMessages[position].translation
            deleteLikedButton.setOnClickListener {
                deleteListener(likedMessages[position])
            }
        }
    }

}