package com.example.homeworkfragments.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.homeworkfragments.App
import com.example.homeworkfragments.Executors
import com.example.homeworkfragments.R
import com.example.homeworkfragments.adapters.LikedAdapter
import com.example.homeworkfragments.database.LikedMessage
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_liked.*


class LikedFragment : Fragment(R.layout.fragment_liked) {
    var prefs: SharedPreferences? = null
    var text:String = ""
    private val executors = Executors()
    var likedList = mutableListOf<LikedMessage>()
    val likedAdapter = LikedAdapter(
        deleteListener = {
            deleteItems(it)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val likedLayoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.VERTICAL,false)

        likedRecyclerView.apply {
            adapter = likedAdapter
            layoutManager = likedLayoutManager
        }

        loadData()
    }


    private fun deleteItems(item : LikedMessage){
        var recentlyDeletedIndex = likedList.indexOf(item)
        var recentlyDeletedItem = item
        executors.diskIO().execute {
            (activity?.applicationContext as App).db.likedDao().delete(item)
        }
        likedList.remove(item)
        likedAdapter.setItems(likedList)
        activity?.mainContainer?.let {
            Snackbar
                .make(it, "Oshirildi: ${item.text}-${item.translation}",2000)
                .setAction("Keri qaitaru"){
                    executors.diskIO().execute {
                        (activity?.applicationContext as? App)?.db?.likedDao()?.insertLiked(item)
                    }
                    likedList.add(recentlyDeletedIndex,recentlyDeletedItem)
                    likedAdapter.setItems(likedList)
                }
                .setTextColor(Color.parseColor("#393737"))
                .setBackgroundTint(Color.parseColor("#D8D8D8"))
                .setActionTextColor(Color.parseColor("#1789DB"))
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        executors.shutdown()
    }

    private fun loadData() {
        executors.diskIO().execute{
            likedList = (activity?.applicationContext as App).db.likedDao().getAll()
        }
        executors.mainThread().execute{
            likedAdapter.setItems(likedList)
        }
    }

}