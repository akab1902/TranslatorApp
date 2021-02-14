package com.example.homeworkfragments.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homeworkfragments.Communicator
import com.example.homeworkfragments.R
import com.example.homeworkfragments.adapters.VideoAdapter
import com.example.homeworkfragments.models.VideoObject
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(R.layout.fragment_search) {

    lateinit var comm: Communicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comm = activity as Communicator

        val videoList = mutableListOf<VideoObject>()

        var videoOne:VideoObject = VideoObject("Komedıia \"Ákіm\" 2020", "https://express-k.kz/upload/iblock/6e7/6e77a70dc68fb78d5ed6625442a0c1fb.jpg","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4", "Представитель «золотой молодежи» Казахстана получает назначение на должность акима глухого поселка. Молодой чиновник мечтает сбежать из бедного аула, пока не влюбляется в местную учительницу. Звезда казахстанского кино, режиссер, сценарист и продюсер Нуртас Адамбай («Келинка Сабина», «Я – жених») представляет душевную и остроумную комедию о наболевшем «Аким», где сам Нуртас также исполнил главную роль.")
        var videoTwo:VideoObject = VideoObject("Melodrama  \"Vody Slonam\" 2011","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUI054zAzeIW5ICMHJeBPzpftbDexOdZM2_w&usqp=CAU", "https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4", "Времена Великой депрессии. Студент-ветеринар Якоб бросает учебу после того, как его родители погибают, и присоединяется к «Benzini Brothers», самому грандиозному цирковому шоу на Земле. Там он начинает работать ветеринаром, а заодно и влюбляется в прекрасную наездницу Марлену, которая, правда, замужем за Августом, харизматичным, но жестоким дрессировщиком.")
        var videoThree:VideoObject = VideoObject("Multfilm  \"Alpamys\" 2011","https://idealbloghub.com/wp-content/uploads/2020/02/Top-best-Android-Apps-to-watch-and-stream-free-movies-online.jpg", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4","Мультфильм, мультипликационный фильм, также анимационный фильм — фильм, выполненный при помощи средств мультипликации, то есть покадрового запечатления созданных художником объёмных и плоских изображений или объектов предметно-реального мира на кино- и видеоплёнке или на цифровых носителях.")

        videoList.add(videoOne)
        videoList.add(videoTwo)
        videoList.add(videoThree)
        Log.d("List Items", videoList.toString())


        val videoadapter = VideoAdapter(
            clickListener = {
                val data = listOf(it.title, it.videoUrl, it.videoDescription)
                comm.passDataCom(data)
            }
        )
        videoadapter.setItems(videoList)
        val videoLayoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.VERTICAL,
            false)

        videoPlayerRecyclerView.apply {
            adapter = videoadapter
            layoutManager = videoLayoutManager
        }
    }
}