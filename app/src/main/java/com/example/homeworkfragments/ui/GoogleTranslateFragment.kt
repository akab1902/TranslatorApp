package com.example.homeworkfragments.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.homeworkfragments.App
import com.example.homeworkfragments.Executors
import com.example.homeworkfragments.R
import com.example.homeworkfragments.adapters.EnglishChatAdapter
import com.example.homeworkfragments.database.LikedMessage
import com.example.homeworkfragments.database.TranslatedMessage
import com.example.homeworkfragments.models.Translation
import com.example.homeworkfragments.network.TranslationApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_english_translate.*


class GoogleTranslateFragment : Fragment(R.layout.fragment_english_translate) {

    private val translationApiService = TranslationApiService.create()
    private val compositeDisposable = CompositeDisposable()
    private var messageList = mutableListOf<TranslatedMessage>()
    var messagePopupMenu: PopupMenu?=null
    private var engChatAdapter = EnglishChatAdapter(clickListener = { text, itemView ->})
    private val executors = Executors()
    private var transDirection = ""
    var chatLayoutManager:LinearLayoutManager? = null

    // scroll listener
    val scrollListener = object :  RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lastVisibleItemPosition = chatLayoutManager?.findLastVisibleItemPosition()?:0
            if (lastVisibleItemPosition <(messageList.size-1)){
                goBottomButton.isVisible = true
            } else if (lastVisibleItemPosition==(messageList.size-1)){
                goBottomButton.isVisible = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transDirection = "KAZ - ENG"

        //RecyclerView Layout and Adapter
        engChatAdapter = EnglishChatAdapter(
            clickListener = {message,itemView ->
                createPopupMenu(message,itemView)
            }
        )
        chatLayoutManager = LinearLayoutManager(requireContext())
        chatLayoutManager?.stackFromEnd=true
        englishTranslateRecyclerView.apply {
            adapter = engChatAdapter
            layoutManager = chatLayoutManager
        }

        // item decoration
        val itemDecoration = LatinTransItemDecoration(10, 20)
        englishTranslateRecyclerView.addItemDecoration(itemDecoration)

        englishTranslateRecyclerView.addOnScrollListener(scrollListener)
        
        goBottomButton.isVisible=false

        //Scroll to bottom
        goBottomButton.setOnClickListener {
            val smoothScroller = object : LinearSmoothScroller(activity) {
                override fun getVerticalSnapPreference(): Int =
                    SNAP_TO_END
            }
            smoothScroller.targetPosition = messageList.size
            chatLayoutManager?.startSmoothScroll(smoothScroller)

            goBottomButton.isVisible = false
        }

        //Change Language
        changeLangTextView.setOnClickListener {
            if (transDirection=="KAZ - ENG"){
                changeLangTextView.text = "Eng - Қаз"
                editText.hint="Hello, how are you?"
                transDirection = "ENG - KAZ"
            } else {
                changeLangTextView.text = "Қаз - Eng"
                transDirection = "KAZ - ENG"
                editText.hint="Сәлем, қалайсың?"
            }
        }

        translateButton.setOnClickListener {
            insertData()
        }

        //Load from Database
        loadData()
    }

    private fun getTranslatedText(text: String,lang:String){
        val disposable = translationApiService.getTranslation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    messageList.add(TranslatedMessage(text = editText.text.toString(),type = "sent"))
                    executors.diskIO().execute {
                        (activity?.applicationContext as App).db.translationDao().insertTranslation(
                            TranslatedMessage(
                                text = editText.text.toString(),
                                type = "sent"
                            )
                        )
                    }
                    engChatAdapter.setMessages(messageList)

                    messageList.add(TranslatedMessage(text = findMatch(it,lang,text), type = "received"))
                    executors.diskIO().execute {
                        (activity?.applicationContext as App).db.translationDao().insertTranslation(
                            TranslatedMessage(
                                text = findMatch(it, lang, text),
                                type = "received"
                            )
                        )
                    }
                    engChatAdapter.setMessages(messageList)
                    editText.text.clear()
                },
                {
                    Log.d(it.message.toString(), "Error get translation")
                }
            )
        compositeDisposable.add(disposable)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        executors.shutdown()
    }

    override fun onPause() {
        super.onPause()
        englishTranslateRecyclerView.removeOnScrollListener(scrollListener)
    }

    private fun loadData(){
        executors.diskIO().execute{
            messageList = (activity?.applicationContext as App).db.translationDao().getAll()
        }
        executors.mainThread().execute{
            engChatAdapter.setMessages(messageList)
        }
    }

    private fun insertData(){
        executors.diskIO().execute{
            if(editText.text.toString()!=""){
                if(transDirection=="ENG - KAZ"){
                    getTranslatedText(editText.text.toString(),"kz")
                } else if(transDirection=="KAZ - ENG"){
                    getTranslatedText(editText.text.toString(),"en")
                }
            }
        }
    }

    fun createPopupMenu(message: TranslatedMessage, itemView:View){
            messagePopupMenu = PopupMenu(context,itemView)
            val popup = PopupMenu::class.java.getDeclaredField(("mPopup"))
            messagePopupMenu?.menuInflater?.inflate(R.menu.dialog_menu, messagePopupMenu?.menu)
            popup.isAccessible = true
            val menu = popup.get(messagePopupMenu)
            menu.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
            messagePopupMenu?.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.copyButton -> {
                        Toast.makeText(activity, "Copy", Toast.LENGTH_SHORT).show()
                        false
                    }
                    R.id.shareButton -> {
                        Toast.makeText(activity, "Share", Toast.LENGTH_SHORT).show()
                        false
                    }
                    R.id.deleteButton -> {
                        Toast.makeText(activity, "Delete", Toast.LENGTH_SHORT).show()
                        executors.diskIO().execute {
                            (activity?.applicationContext as App).db.translationDao().delete(message)
                        }
                        messageList.remove(message)
                        engChatAdapter.setMessages(messageList)
                        false
                    }
                    R.id.likeButton -> {
                        executors.diskIO().execute{
                            //Adding text in liked translation to database
                            if (message.type=="sent"){
                                var msg = (activity?.applicationContext as App).db.translationDao().loadbyId((message.id+1).toString())
                                (activity?.applicationContext as App).db.likedDao().insertLiked(
                                    LikedMessage(text = message.text, translation = msg.text)
                                )
                                executors.mainThread().execute{
                                    Toast.makeText(activity, "Saved ${message.text} - ${msg.text} ", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                var msg = (activity?.applicationContext as App).db.translationDao().loadbyId((message.id-1).toString())
                                (activity?.applicationContext as App).db.likedDao().insertLiked(
                                    LikedMessage(text = msg.text, translation = message.text)
                                )
                                executors.mainThread().execute{
                                    Toast.makeText(activity, "Saved ${msg.text} - ${message.text} ", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        false
                    }
                    else ->{
                        false
                    }
                }
            }
            messagePopupMenu?.show()
        }
    }


fun findMatch(list:List<Translation>, lang: String, text: String):String{
    when (lang){
        "kz"->{
            var i=0
            while (i<(list.size-1) && list[i].en!=text){
                i++
            }
            return if(i==list.size){
                "Not found"
            }else {
                list[i].kz
            }
        }
        "en"->{
            var i=0
            while (i<(list.size-1) && list[i].kz!=text){
                i++
            }
            return if(i==list.size-1){
                "Not found"
            }else {
                list[i].en
            }
        }
    }
    return "Not Found"
}




