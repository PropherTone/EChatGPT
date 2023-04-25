package com.protone.eChatGPT.repository

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.R
import com.protone.eChatGPT.repository.dataStore.DataDelegate
import com.protone.eChatGPT.repository.dataStore.DataStoreTool

val userConfig = UserConfig(EApplication.app)

class UserConfig(context: Context) {

    private val Context.dataProvider by preferencesDataStore(name = "USER_CONFIG")

    private val config = DataStoreTool(DataDelegate(context.dataProvider))

    var token by config.string("OpenAI-Token", "")

    private val shortcut by config.string(
        "Notification_ShortCuts",
        "${Shortcut.Chat}|${Shortcut.Config}"
    )
    val notificationShortcuts
        get() = shortcut.split("|").map { Shortcut(it) }

    @JvmInline
    value class Shortcut(val mode: String) {
        companion object {
            val Chat: Shortcut = Shortcut("Mode_Chat")
            val Config: Shortcut = Shortcut("Mode_Config")
            val Image: Shortcut = Shortcut("Mode_Image")
            val History: Shortcut = Shortcut("Mode_History")
            val Audio: Shortcut = Shortcut("Mode_Audio")
            val Completions: Shortcut = Shortcut("Mode_Completions")
            val Edits: Shortcut = Shortcut("Mode_Edits")
            val Embeddings: Shortcut = Shortcut("Mode_Embeddings")
            val Files: Shortcut = Shortcut("Mode_Files")
            val FineTunes: Shortcut = Shortcut("Mode_FineTunes")
            val Models: Shortcut = Shortcut("Mode_Models")
            val Moderations: Shortcut = Shortcut("Mode_Moderations")

            fun Shortcut.getResource() = when (this) {
                Config -> R.drawable.chart
                Image -> R.drawable.image
                History -> R.drawable.history
                Audio -> R.drawable.audio
                Completions -> R.drawable.completions
                Edits -> R.drawable.edit
                Embeddings -> R.drawable.embeddings
                Files -> R.drawable.file
                FineTunes -> R.drawable.tune
                Models -> R.drawable.model
                Moderations -> R.drawable.moderation
                else -> R.drawable.chat
            }

        }
    }

    data class UserData(
        val token: String,
    )

    fun getJson() = this.run {
        val userData = UserData(token)
        userData
    }

}