package com.sivsa.omnitv.tools

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import es.dmoral.toasty.Toasty
import java.util.*

fun Context.isAndroidTV(): Boolean {
    val uiModeManager: UiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isNativeAndroidTV = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    val hasBattery = hasBattery()

    return isNativeAndroidTV || !hasBattery
}

fun Context.hasBattery(): Boolean {
    val level = getBatteryLevel()
    return if (level > 0) {
        true
    } else {
        // Los dispositivos para Hula pueden tener capacidades 3G por lo que se comprueba el modelo
        if (TabletsWithoutBattery.isSpecialDevice()) {
            true
        } else {
            isPhone(this)
        }
    }
}

fun Context.getBatteryLevel(): Int {
    val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}

fun isPhone(context: Context): Boolean {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    telephonyManager.let {
        when (it.phoneType) {
            TelephonyManager.PHONE_TYPE_CDMA -> Log.d("isPhone","Is CDMA")
            TelephonyManager.PHONE_TYPE_GSM -> Log.d("isPhone", "Is GSM")
            TelephonyManager.PHONE_TYPE_SIP -> Log.d("isPhone", "Is SIP")
            else -> {
                return false
            }
        }
    }

    return true
}

object TabletsWithoutBattery {

    private val listSupportedModels = listOf("rk3288")

    fun isSpecialDevice(): Boolean = listSupportedModels.contains(Build.MODEL.toLowerCase(Locale.getDefault()))
}

enum class TypeToasty {
    SUCCESS, ERROR, INFO, WARNING
}

fun Context.toast(
    value_message: Any?,
    duration: Int = Toast.LENGTH_LONG,
    type: TypeToasty = TypeToasty.INFO
) {
    var message: CharSequence? = null

    if (value_message is CharSequence) {
        message = value_message
    } else if (value_message is Int) {
        message = getString(value_message)
    }

    if (!message.isNullOrEmpty()) {
        uiThread {

            when (type) {
                TypeToasty.SUCCESS -> Toasty.success(this, message, duration, true).show()
                TypeToasty.ERROR -> Toasty.error(this, message, duration, true).show()
                TypeToasty.INFO -> Toasty.info(this, message, duration, true).show()
                TypeToasty.WARNING -> Toasty.warning(this, message, duration, true).show()
            }
        }
    }
}

fun uiThread(f: () -> Unit) {
    if (ContextHelper.uiThread == Thread.currentThread()) {
        f()
    } else {
        ContextHelper.handler.post(f)
    }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
    val uiThread = Looper.getMainLooper().thread
}
