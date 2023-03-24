package com.protone.eChatGPT.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatHistory(
    @ColumnInfo(name = "history_storage_path")
    val path: String,
    @ColumnInfo(name = "saved_date")
    val date: Long
) {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
}