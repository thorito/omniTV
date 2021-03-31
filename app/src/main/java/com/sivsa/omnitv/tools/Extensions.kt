package com.sivsa.omnitv.tools

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.sivsa.omnitv.commons.MyBaseApplication
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

fun toast(
    value_message: Any?,
    duration: Int = Toast.LENGTH_LONG,
    type: TypeToasty = TypeToasty.INFO
) {
    val ctx = MyBaseApplication.instance.applicationContext
    ctx.toast(value_message = value_message, duration = duration, type = type)
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

fun isPortraitOrientation(configuration: Configuration) =
    configuration.orientation == Configuration.ORIENTATION_PORTRAIT

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
    val uiThread = Looper.getMainLooper().thread
}

fun EditText.afterTextChanged(callback: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(editable: Editable?) {
            callback(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            /**
             * No se utiliza. La funcion se creó para simplificar el código
             */
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            /**
             * No se utiliza. La método se creó para simplificar el código
             */
        }
    })
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}