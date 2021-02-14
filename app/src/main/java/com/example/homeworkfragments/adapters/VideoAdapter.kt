package com.example.homeworkfragments.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homeworkfragments.R
import com.example.homeworkfragments.models.VideoObject
import kotlinx.android.synthetic.main.item_video_preview.view.*

private val videos = mutableListOf<VideoObject>()

class VideoAdapter(
    private val clickListener: (item: VideoObject) -> Unit
    ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return VideoViewHolder(
            inflater,
            parent
        )
    }

    override fun getItemCount() = videos.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VideoViewHolder).bind(
            videos[position], clickListener, position)
    }

    fun setItems(list:MutableList<VideoObject>){
        videos.clear()
        videos.addAll(list)
        notifyDataSetChanged()
    }


    private class VideoViewHolder(inflater: LayoutInflater,parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_video_preview,parent, false))
    {
        private val previewPlayButton = itemView.previewPlayButton
        private val previewTitle = itemView.previewTitle
        private val thumbnail = itemView.videoPlayerImageView
        private val previewTitleBox = itemView.previewTitleBox

        fun bind(item: VideoObject, clickListener: (item: VideoObject) -> Unit, position: Int){
            previewTitle.text=item.title
            Glide.with(itemView.context).load(item.thumbnailUrl).centerCrop().placeholder(android.R.color.black)
                .error(R.color.colorPrimary).into(thumbnail)
            previewPlayButton.setOnClickListener {
                clickListener(videos[position])
            }
            thumbnail.setOnClickListener {
                clickListener(videos[position])
            }
            previewTitleBox.setOnClickListener {
                clickListener(videos[position])
            }
        }

    }


}