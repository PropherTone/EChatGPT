package com.protone.eChatGPT.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "chat_history_group")
    val group: String,
    @ColumnInfo(name = "history")
    val content: String,
    @ColumnInfo(name = "saved_date")
    val date: Long
) {
    constructor(group: String, content: String, date: Long) : this(0L, group, content, date)
}