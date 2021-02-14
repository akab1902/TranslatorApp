package com.example.homeworkfragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.homeworkfragments.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),Communicator {

    override fun onCreate(savedInstanceState: Bundle?) {
        val(theme, icon) = if(getNightMode()){
            Pair(R.style.AppThemeDark, R.drawable.ic_sun)
        } else {
            Pair(R.style.AppTheme, R.drawable.ic_moon)
        }

        setTheme(theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeThemeButton.setImageResource(icon)
        changeThemeButton.setOnClickListener {
            switchNightMode()
            recreate()
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.translate_nav
            val fragment =
                LatinTranslateFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerFragment,
                    fragment
                )
                .commit()
        }

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                toolBarTitle.text = item.title
                when (item.itemId) {
                    R.id.like_nav -> {
                        toolbar3.isVisible = true
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.containerFragment,
                                LikedFragment()
                            )
                            .addToBackStack(LikedFragment::class.java.simpleName)
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.translate_nav -> {
                        toolbar3.isVisible = true
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.containerFragment,
                                LatinTranslateFragment()
                            )
                            .addToBackStack(LatinTranslateFragment::class.java.simpleName)
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.google_translate_nav -> {
                        toolbar3.isVisible = false
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.containerFragment,
                                GoogleTranslateFragment()
                            )
                            .addToBackStack(LatinTranslateFragment::class.java.simpleName)
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.account_nav -> {
                        toolbar3.isVisible = true
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.containerFragment,
                                AccountFragment()
                            )
                            .addToBackStack(LatinTranslateFragment::class.java.simpleName)
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.search_nav -> {
                        toolbar3.isVisible = true
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.containerFragment,
                                SearchFragment()
                            )
                            .addToBackStack(LatinTranslateFragment::class.java.simpleName)
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }

                }
                false
            }
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //Tool bar Pop Up Menu
        val morePopupMenu = PopupMenu(
            this,
            popUpMenuButton
        )

        morePopupMenu.menuInflater.inflate(R.menu.toolbar_popup_menu, morePopupMenu.menu)
        morePopupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.emailPopupItem -> {
                    val email = Intent(Intent.ACTION_SENDTO)
                    email.data = Uri.parse("mailto:jumysbar@gmail.com")
                    email.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                    email.putExtra(Intent.EXTRA_TEXT, "My Email message")
                    startActivity(email)
                    //Toast.makeText(this, "Email to author", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.sharePopupItem -> {
                    Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                    false
                }
                R.id.qosymshaPopupItem -> {
                    Toast.makeText(this, "Qosymsha", Toast.LENGTH_SHORT).show()
                    false
                }
                else ->{
                    false
                }
            }
        }
        popUpMenuButton.setOnClickListener {

            val popup = PopupMenu::class.java.getDeclaredField(("mPopup"))
            popup.isAccessible = true
            val menu = popup.get(morePopupMenu)
            menu.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)

            morePopupMenu.show()
        }

    }

    override fun passDataCom(input: List<String>) {
        val bundle = Bundle()
        bundle.putString("title",input[0])
        bundle.putString("videoUrl",input[1])
        bundle.putString("videoDescription",input[2])

        val transaction = this.supportFragmentManager.beginTransaction()
        val frag2 = VideoPlayerFragment()
        frag2.arguments = bundle

        transaction.add(R.id.containerFragment2, frag2)
            .addToBackStack(SearchFragment::class.java.simpleName)
        transaction.commit()
    }
//
//    fun startChronometer(startTime: Long){
//        val timeInMilSeconds = startTime
//        chronometer.base = SystemClock.elapsedRealtime() - timeInMilSeconds
//        chronometer.start()
//    }
//
//    fun stopChronometer(){
//        var timerTime = SystemClock.elapsedRealtime() - chronometer.getBase();
//        Toast.makeText(this, timerTime.toString(), Toast.LENGTH_SHORT).show()
//        chronometer.stop()
//    }
}

