package com.example.homeworkfragments.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_table")
data class TranslatedMessage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int =0,
    @ColumnInfo(name = COLUMN_TEXT)
    val text: String,
    @ColumnInfo(name = COLUMN_TYPE)
    val type: String
)

{
    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_TEXT = "text"
        const val COLUMN_TYPE = "type"
    }
}

