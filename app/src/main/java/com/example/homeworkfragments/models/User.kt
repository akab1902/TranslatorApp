package com.example.homeworkfragments.models

data class User(
    val name: Any,
    val email: Any,
    val words_translated: Any,
    val time: Any
)

class UserConverter{
    fun convert(hashMap: HashMap<String,Any>): User =
        User(hashMap["name"]?:"",
            hashMap["name"]?:"",
            hashMap["words"]?:"",
            hashMap["time"]?:"")
}