package com.protone.eChatGPT.repository.dataBase

import androidx.paging.PagingSource
import androidx.room.*
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.bean.ChatItem

@Dao
interface ChatHistoryDAO {

    @Insert
    suspend fun insertChatHistory(chatHistory: ChatHistory)

    @Insert
    suspend fun insertChatHistories(chatHistories: List<ChatHistory>)

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistory)

    @Update
    suspend fun updateChatHistory(chatHistory: ChatHistory)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM ChatHistory")
    suspend fun getAllChatHistory(): List<ChatHistory>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM ChatHistory")
    fun getPagingSource(): PagingSource<Int, ChatHistory>

    @Query("SELECT count(chat_history_group) FROM ChatHistory WHERE chat_history_group IS :group")
    fun getChatHistoryByName(group: String): Int

}
