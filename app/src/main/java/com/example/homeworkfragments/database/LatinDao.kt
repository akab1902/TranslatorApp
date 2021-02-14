package com.example.homeworkfragments.database

import androidx.room.*


@Dao
interface LatinDao {

    @Query("SELECT * FROM latin_table")
    fun getAll(): MutableList<LatinMessage>

    @Query("SELECT * FROM latin_table WHERE id=:id ")
    fun loadbyId(id: String): LatinMessage

    @Query("SELECT COUNT(*) FROM latin_table")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(translations: MutableList<LatinMessage>)

    @Delete
    fun delete(translatedMessage: LatinMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTranslation(translation: LatinMessage)

}