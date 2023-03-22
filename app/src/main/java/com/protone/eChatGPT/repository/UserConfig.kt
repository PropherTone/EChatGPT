package com.protone.eChatGPT.repository

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.repository.dataStore.DataDelegate
import com.protone.eChatGPT.repository.dataStore.DataStoreTool

val userConfig = UserConfig(EApplication.app)

class UserConfig(context: Context) {

    private val Context.dataProvider by preferencesDataStore(name = "USER_CONFIG")

    private val config = DataStoreTool(DataDelegate(context.dataProvider))

    var token by config.string("OpenAI-Token", "")

    data class UserData(
        val token: String,
    )

    fun getJson() = this.run {
        val userData = UserData(token)
        userData
    }

}