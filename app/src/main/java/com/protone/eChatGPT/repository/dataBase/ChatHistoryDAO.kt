package com.protone.eChatGPT.repository.dataBase

import androidx.paging.PagingSource
import androidx.room.*
import com.protone.eChatGPT.bean.ChatHistory

@Dao
interface ChatHistoryDAO {

    @Insert
    suspend fun insertChatHistory(chatHistory: ChatHistory)

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistory)

    @Update
    suspend fun updateChatHistory(chatHistory: ChatHistory)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM ChatHistory")
    suspend fun getAllChatHistory(): List<ChatHistory>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM ChatHistory")
    suspend fun getPagingSource(): PagingSource<Long, ChatHistory>

}
