package com.mandadhi.voicecalc

import android.app.Application
import android.content.Context

class AppContext : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    companion object {
        var context: Context? = null
    }
}
