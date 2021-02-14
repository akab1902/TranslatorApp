package com.example.homeworkfragments.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.os.HandlerThread
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
import com.example.homeworkfragments.adapters.LatinChatAdapter
import com.example.homeworkfragments.database.LatinMessage
import com.example.homeworkfragments.database.LikedMessage
import com.example.homeworkfragments.network.TranslationApiService
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_latin_translate.*


class LatinTranslateFragment : Fragment(R.layout.fragment_latin_translate) {

    private val translationApiService = TranslationApiService.create()
    private val disposable = CompositeDisposable()
    private var messageList = mutableListOf<LatinMessage>()
    var messagePopupMenu: PopupMenu? = null
    private var latinChatAdapter = LatinChatAdapter(clickListener = { text, itemView -> })
    private val executors = Executors()
    var chatLayoutManager: LinearLayoutManager? = null
    private var translatedText = ""
    private var translateThread: HandlerThread? = null

    val compositeDisposable = CompositeDisposable()


    // scroll listener
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lastVisibleItemPosition = chatLayoutManager?.findLastVisibleItemPosition() ?: 0
            if (lastVisibleItemPosition < (messageList.size - 1)) {
                goBottomButton.isVisible = true
            } else if (lastVisibleItemPosition == (messageList.size - 1)) {
                goBottomButton.isVisible = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RecyclerView Layout and Adapter
        latinChatAdapter = LatinChatAdapter(
            clickListener = { message, itemView ->
                createPopupMenu(message, itemView)
            }
        )

        chatLayoutManager = LinearLayoutManager(requireContext())
        chatLayoutManager?.stackFromEnd = true
        latinTranslateRecyclerView.apply {
            adapter = latinChatAdapter
            layoutManager = chatLayoutManager
        }

        // item decoration
        val itemDecoration = LatinTransItemDecoration(10, 20)
        latinTranslateRecyclerView.addItemDecoration(itemDecoration)

        latinTranslateRecyclerView.addOnScrollListener(scrollListener)

        goBottomButton.isVisible = false

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

        sendButton.setOnClickListener {
            insertData()
        }

        //Load from Database
        loadData()
    }

    override fun onDestroyView() {
        disposable.dispose()
        executors.shutdown()
        translateThread?.quit()
        compositeDisposable.clear()
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        latinTranslateRecyclerView.removeOnScrollListener(scrollListener)
    }

    private fun loadData() {
        val disposable = Completable.fromAction {
        executors.diskIO().execute {
            messageList = (activity?.applicationContext as App).db.latinDao().getAll()
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    latinChatAdapter.setMessages(messageList)
                    Log.e("App", "Loaded")
                },
                {
                    Log.e("App", "Error occurred", it)
                    Toast.makeText(activity, "Error occurred", Toast.LENGTH_SHORT).show()
                }
            )
        compositeDisposable.add(disposable)
    }

    private fun insertData() {
        //Executing translation on another thread
        translateThread = HandlerThread("TranslateHandlerThread")
        translateThread?.let { thread ->
            thread.start()
            translatedText = translate(EditText.text.toString())
        }
        messageList.add(LatinMessage(text = EditText.text.toString(), type = "sent"))
        messageList.add(LatinMessage(text = translatedText, type = "received"))

        val disposable = Completable.fromAction {
            executors.diskIO().execute {
                //Adding text in kirillica to database
                (activity?.applicationContext as App).db.latinDao().insertTranslation(
                    LatinMessage(text = EditText.text.toString(), type = "sent")
                )
                //Adding text in latin to database
                (activity?.applicationContext as App).db.latinDao().insertTranslation(
                    LatinMessage(text = translatedText, type = "received")
                )
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.e("App", "Inserted")
                },
                {
                    Log.e("App", "Error occurred", it)
                }
            )

        compositeDisposable.add(disposable)

        latinChatAdapter.setMessages(messageList)
        EditText.text.clear()
    }

    fun createPopupMenu(message: LatinMessage, itemView: View) {
        messagePopupMenu = PopupMenu(context, itemView)
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
                    Toast.makeText(activity, "Copy ${message.text}", Toast.LENGTH_SHORT).show()
                    copyToClipboard(message.text)
                    false
                }
                R.id.shareButton -> {
                    Toast.makeText(activity, "Share", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.deleteButton -> {
                    Toast.makeText(activity, "Delete", Toast.LENGTH_SHORT).show()
                    executors.diskIO().execute {
                        (activity?.applicationContext as App).db.latinDao().delete(message)
                    }
                    messageList.remove(message)
                    latinChatAdapter.setMessages(messageList)
                    false
                }
                R.id.likeButton -> {
                    executors.diskIO().execute {
                        // Adding text in liked translation to database
                        if (message.type == "sent") {
                            var msg = (activity?.applicationContext as App).db.latinDao()
                                .loadbyId((message.id + 1).toString())
                            (activity?.applicationContext as App).db.likedDao().insertLiked(
                                LikedMessage(text = message.text, translation = msg.text)
                            )
                            executors.mainThread().execute {
                                Toast.makeText(
                                    activity,
                                    "Saved ${message.text} - ${msg.text}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            var msg = (activity?.applicationContext as App).db.latinDao()
                                .loadbyId((message.id - 1).toString())
                            (activity?.applicationContext as App).db.likedDao().insertLiked(
                                LikedMessage(text = msg.text, translation = message.text)
                            )
                            executors.mainThread().execute {
                                Toast.makeText(
                                    activity,
                                    "Saved ${msg.text} - ${message.text}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    false
                }
                else -> {
                    false
                }
            }
        }
        messagePopupMenu?.show()
    }


    private fun copyToClipboard(text: String){
        val myClipboard: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copyied text", text)
        myClipboard.setPrimaryClip(clip)
    }

}



fun translate(orgText: String): String {
    val trslText = mutableListOf<String>()
    orgText.forEachIndexed { _, c ->
        when (c) {
            'А' -> trslText.add("A")
            'a' -> trslText.add("a")
            'Ә' -> trslText.add("Á")
            'ә' -> trslText.add("á")
            'Б' -> trslText.add("B")
            'б' -> trslText.add("b")
            'В' -> trslText.add("V")
            'в' -> trslText.add("v")
            'Г' -> trslText.add("G")
            'г' -> trslText.add("g")
            'Ғ' -> trslText.add("Ǵ")
            'ғ' -> trslText.add("ǵ")
            'Д' -> trslText.add("D")
            'д' -> trslText.add("d")
            'Е' -> trslText.add("E")
            'е' -> trslText.add("e")
            'Ё' -> trslText.add("E")
            'ё' -> trslText.add("e")
            'Ж' -> trslText.add("J")
            'ж' -> trslText.add("j")
            'З' -> trslText.add("Z")
            'з' -> trslText.add("z")
            'И' -> trslText.add("I")
            'и' -> trslText.add("ı")
            'Й' -> trslText.add("I")
            'й' -> trslText.add("ı")
            'К' -> trslText.add("K")
            'к' -> trslText.add("k")
            'Қ' -> trslText.add("Q")
            'қ' -> trslText.add("q")
            'Л' -> trslText.add("L")
            'л' -> trslText.add("l")
            'М' -> trslText.add("M")
            'м' -> trslText.add("m")
            'Н' -> trslText.add("N")
            'н' -> trslText.add("n")
            'Ң' -> trslText.add("Ń")
            'ң' -> trslText.add("ń")
            'О' -> trslText.add("O")
            'о' -> trslText.add("o")
            'Ө' -> trslText.add("Ó")
            'ө' -> trslText.add("ó")
            'П' -> trslText.add("P")
            'п' -> trslText.add("p")
            'Р' -> trslText.add("R")
            'р' -> trslText.add("r")
            'С' -> trslText.add("S")
            'с' -> trslText.add("s")
            'Т' -> trslText.add("T")
            'т' -> trslText.add("t")
            'У' -> trslText.add("Ý")
            'у' -> trslText.add("ý")
            'Ұ' -> trslText.add("U")
            'ұ' -> trslText.add("u")
            'Ү' -> trslText.add("Ú")
            'ү' -> trslText.add("ú")
            'Ф' -> trslText.add("F")
            'ф' -> trslText.add("f")
            'Х' -> trslText.add("H")
            'х' -> trslText.add("h")
            'Һ' -> trslText.add("H")
            'һ' -> trslText.add("h")
            'Ц' -> trslText.add("C")
            'ц' -> trslText.add("c")
            'Ч' -> trslText.add("Ch")
            'ч' -> trslText.add("ch")
            'Ш' -> trslText.add("Sh")
            'ш' -> trslText.add("sh")
            'Щ' -> trslText.add("Sh")
            'щ' -> trslText.add("sh")
            'Ъ' -> trslText.add("")
            'ъ' -> trslText.add("")
            'Ы' -> trslText.add("Y")
            'ы' -> trslText.add("y")
            'І' -> trslText.add("I")
            'і' -> trslText.add("i")
            'Ь' -> trslText.add("")
            'ь' -> trslText.add("")
            'Э' -> trslText.add("E")
            'э' -> trslText.add("e")
            'Ю' -> trslText.add("yu")
            'ю' -> trslText.add("ıý")
            'Я' -> trslText.add("Ia")
            'я' -> trslText.add("ıa")
            else -> trslText.add(c.toString())
        }
    }
    return trslText.joinToString(separator = "")
} // Функция перевода кириллицы на латиницу




