package com.example.homeworkfragments.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.homeworkfragments.R
import kotlinx.android.synthetic.main.item_video_onview.*

class VideoPlayerFragment:Fragment(R.layout.item_video_onview) {

    var titleText: String? = ""
    var videoUrlText: String? = ""
    var videoDescriptionText: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = arguments?.getString("title")
        videoUrlText = arguments?.getString("videoUrl")
        videoDescriptionText = arguments?.getString("videoDescription")
        bottomTitleTextView.text = titleText
        descTextView.text = videoDescriptionText
        videoPlayerView.setTitle(titleText)
        videoPlayerView.play(videoUrlText)
//        videoPlayerView.seekBar.setOnClickListener {
//            videoPlayerView.playerClick()
//        }
    }

    override fun onDestroy() {
        videoPlayerView.destroy()
        super.onDestroy()
    }
}