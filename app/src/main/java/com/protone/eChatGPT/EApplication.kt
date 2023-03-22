package com.protone.eChatGPT

import android.app.Application

class EApplication : Application() {

    @Suppress("ObjectPropertyName")
    companion object {
        private lateinit var _app: Application

        @JvmStatic
        val app get() = _app
    }

    override fun onCreate() {
        super.onCreate()
        _app = this
    }
}