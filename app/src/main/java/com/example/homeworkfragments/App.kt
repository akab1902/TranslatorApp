package com.example.homeworkfragments

import android.app.Application
import com.example.homeworkfragments.database.TranslationDatabase


class App : Application() {

    lateinit var db: TranslationDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = TranslationDatabase.getInstance(this)
    }
}