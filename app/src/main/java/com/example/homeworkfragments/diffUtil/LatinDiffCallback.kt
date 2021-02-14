package com.example.homeworkfragments.diffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.homeworkfragments.database.LatinMessage

class LatinDiffCallback: DiffUtil.Callback() {
    private var oldList = emptyList<LatinMessage>()
    private var newList = emptyList<LatinMessage>()

    fun setItems(oldList: List<LatinMessage>, newList: List<LatinMessage>) {
        this.oldList = oldList
        this.newList = newList
    }


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id


    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition] == newList[newPosition]
    }


    //Payload
//    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
//        val fields = mutableSetOf<StudentPayload>()
//        val oldItem = oldList[oldPosition]
//        val newItem = newList[newPosition]
//
//        if (oldItem.id != newItem.id) fields.add(StudentPayload.ID)
//        if (oldItem.firstname != newItem.firstname) fields.add(StudentPayload.FIRST_NAME)
//        if (oldItem.lastname != newItem.lastname) fields.add(StudentPayload.LAST_NAME)
//        if (oldItem.classes != newItem.classes) fields.add(StudentPayload.CLASSES)
//
//        return when {
//            fields.isNotEmpty() -> fields
//            else -> super.getChangePayload(oldPosition, newPosition) // null
//        }
//    }


}