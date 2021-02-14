package com.example.homeworkfragments.ui

import android.content.Context
import android.content.SharedPreferences
import kotlinx.android.synthetic.main.activity_main.*

const val PREF_NAME = "THEME_PREFERENCES"
const val NIGHT_MODE = "NIGHT_MODE"

private fun Context.getPreferences(): SharedPreferences = getSharedPreferences(PREF_NAME, 0)

fun Context.switchNightMode(){
    val nightMode = !getNightMode()
    getPreferences().edit().putBoolean(NIGHT_MODE, nightMode).apply()
}

fun Context.getNightMode() = getPreferences().getBoolean(NIGHT_MODE, false)