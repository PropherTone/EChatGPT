package com.protone.eChatGPT.repository.dataBase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.bean.ChatHistory
import java.lang.ref.SoftReference

val chatHistoryDAO get() = ChatDataBase.database.getChatHistoryDAO()

@Database(entities = [ChatHistory::class], version = 1, exportSchema = false)
abstract class ChatDataBase : RoomDatabase() {
    abstract fun getChatHistoryDAO(): ChatHistoryDAO

    companion object {
        @JvmStatic
        val database: ChatDataBase
            @Synchronized get() {
                return databaseImpl?.get() ?: init().apply {
                    databaseImpl = SoftReference(this)
                }
            }

        private var databaseImpl: SoftReference<ChatDataBase>? = null

        private fun init(): ChatDataBase {
            return Room.databaseBuilder(
                EApplication.app,
                ChatDataBase::class.java,
                "EChatDB"
            ).build()
        }
    }
}