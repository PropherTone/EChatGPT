package com.protone.eChatGPT.repository.dataBase

import androidx.paging.PagingSource
import androidx.room.*
import com.protone.eChatGPT.bean.ChatHistory

@Dao
interface ChatHistoryDAO {

    @Insert
    suspend fun insertChatHistory(chatHistory: ChatHistory)

    @Insert
    suspend fun insertChatHistories(chatHistories: List<ChatHistory>)

    @Delete
    suspend fun deleteChatHistory(chatHistory: ChatHistory): Int

    @Ignore
    @Transaction
    suspend fun deleteChatHistories(chatHistories: Collection<ChatHistory>) =
        chatHistories.filter { deleteChatHistory(it) >= 0 }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateChatHistory(chatHistory: ChatHistory)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM ChatHistory")
    suspend fun getAllChatHistory(): List<ChatHistory>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM ChatHistory")
    fun getPagingSource(): PagingSource<Int, ChatHistory>

    @Query("SELECT count(chat_history_group) FROM ChatHistory WHERE chat_history_group IS :group")
    suspend fun getChatHistoryCountByName(group: String): Int

    @Query("SELECT * FROM ChatHistory WHERE chat_history_group IS :group")
    suspend fun getChatHistoryByName(group: String): ChatHistory

}
