package com.example.homeworkfragments.database

import androidx.room.*

@Dao
interface LikedDao {

    @Query("SELECT * FROM liked_table")
    fun getAll(): MutableList<LikedMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(translations: MutableList<LikedMessage>)

    @Delete
    fun delete(translatedMessage: LikedMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLiked(translation: LikedMessage)

}