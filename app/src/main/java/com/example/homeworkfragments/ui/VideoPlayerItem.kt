package com.example.homeworkfragments.ui

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.example.homeworkfragments.R
import kotlinx.android.synthetic.main.item_video_player.view.*

class VideoPlayerItem
@JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
FrameLayout(context, attrs, defStyleAttr){

    private var isSeekBarVisible = false
    private lateinit var runnable: Runnable
    private lateinit var hide : Runnable
    private var handlerView = android.os.Handler()
    private var playing : Boolean = false


    init {
        View.inflate(context,R.layout.item_video_player, this)
        attrs?.let {
            val typedArray:TypedArray = context.obtainStyledAttributes(
                it,
                R.styleable.VideoPlayerItem,
                defStyleAttr,
                0
            )
            isSeekBarVisible= typedArray.getBoolean(
                R.styleable.VideoPlayerItem_isSeekBarVisible,
                isSeekBarVisible
            )
            seekBar.isVisible = true
            typedArray.recycle()

        }

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                playerClick()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                videoView.seekTo(seekBar?.progress?:0)
                if(playButton.isVisible){
                    videoView.pause()
                } else {
                    videoView.start()
                }
            }
        })
    }

    fun play(url: String?){
        videoView.setVideoPath(url)
        videoView.setOnErrorListener { mp, what, extra ->
            mp
            false
        }

        hide = Runnable {
            titleTextView.visibility = View.GONE
            durationTextView.visibility = View.GONE
            //seekBar.visibility = View.GONE
            pauseButton.visibility=View.GONE
            playButton.visibility=View.GONE
        }

        pauseButton.isVisible = true
        playButton.isVisible = false
        playing=true
        videoView.start()
        handlerView.postDelayed(hide, 3000)

        pauseButton.setOnClickListener {
            playing=false
            videoView.pause()
            pauseButton.isVisible = false
            playButton.isVisible = true
        }
        playButton.setOnClickListener {
            playing=true
            videoView.start()
            pauseButton.isVisible = true
            playButton.isVisible = false
        }
        videoView.setOnPreparedListener {
            seekBar.max = it.duration
            durationTextView.text = "00:00-${formatTime(it.duration)}"
            listenPlayerTrack()
        }
    }

    fun setTitle(title:String?){
        titleTextView.text = title
    }

    fun playerClick(){
        if (titleTextView.visibility == View.VISIBLE) {
            handlerView.postDelayed(hide, 0)
        } else {
            titleTextView.visibility = View.VISIBLE
            durationTextView.visibility = View.VISIBLE
            seekBar.visibility = View.VISIBLE
            if (playing) {
                pauseButton.visibility = View.VISIBLE
            } else {
                playButton.visibility = View.VISIBLE
            }
            handlerView.postDelayed(hide, 3000)
        }
    }

    private fun listenPlayerTrack(){
        runnable = Runnable {
            seekBar.progress = videoView.currentPosition
            durationTextView.text = "${formatTime(videoView.currentPosition)}-${formatTime(videoView.duration)}"
            handlerView.postDelayed(runnable, 100)
        }
        handlerView.postDelayed(runnable, 100)
    }

    fun destroy(){
        handlerView.removeCallbacks(runnable)
        handlerView.removeCallbacks(hide)
    }

}

fun formatTime (duration:Int):String{
    val minutes = duration / 60000;
    val seconds = (duration-(minutes*60000)) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}