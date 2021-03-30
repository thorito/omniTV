package com.sivsa.omnitv.commons

import android.app.Application
import com.sivsa.omnitv.tools.isAndroidTV

class MyBaseApplication: Application() {

    companion object {
        lateinit var instance: MyBaseApplication

        var isTVBox: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        isTVBox = isAndroidTV()
    }


}