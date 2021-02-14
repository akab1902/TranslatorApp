package com.example.homeworkfragments.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TranslatedMessage::class, LatinMessage::class, LikedMessage::class], version = 2, exportSchema = false)
abstract class TranslationDatabase: RoomDatabase(){

    abstract fun translationDao(): TranslationDao
    abstract fun latinDao(): LatinDao
    abstract fun likedDao(): LikedDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: TranslationDatabase? = null

        fun getInstance(context: Context): TranslationDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): TranslationDatabase =
            Room.databaseBuilder(
                context,
                TranslationDatabase::class.java, "translation-database"
            )
                // .addMigrations(MigrationFromVersion1ToVersion2())
                .fallbackToDestructiveMigration()
                .build()
    }

}