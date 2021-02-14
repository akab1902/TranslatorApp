package com.example.homeworkfragments.database

import androidx.room.*

@Dao
interface TranslationDao {

    @Query("SELECT * FROM translation_table")
    fun getAll(): MutableList<TranslatedMessage>

    @Query("SELECT * FROM translation_table WHERE id=:id ")
    fun loadbyId(id: String): TranslatedMessage

    @Query("SELECT COUNT(*) FROM translation_table")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(translations: MutableList<TranslatedMessage>)

    @Delete
    fun delete(translatedMessage: TranslatedMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTranslation(translation: TranslatedMessage)

}