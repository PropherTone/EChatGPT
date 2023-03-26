package com.protone.eChatGPT.utils

class GainData {
    val gainData by lazy { arrayOfNulls<Pair<String, Any>>(1) }

    inline fun <reified T> get(key: String): T? {
        return gainData[0]?.let {
            if (it.first == key) {
                it.second as T
            } else null
        }.also { gainData[0] = null }
    }

    fun put(key: String, data: Any) {
        gainData[0] = Pair(key, data)
    }

}