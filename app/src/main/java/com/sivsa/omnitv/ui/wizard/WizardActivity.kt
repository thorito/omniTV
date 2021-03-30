package com.sivsa.omnitv.ui.wizard

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sivsa.omnitv.R
import com.sivsa.omnitv.commons.MyBaseApplication
import com.sivsa.omnitv.tools.TypeToasty
import com.sivsa.omnitv.tools.toast

class WizardActivity : FragmentActivity() {

    private var hasPermissions: Boolean = false
    private var fromBoot = false

    companion object {
        private const val RC_INSTALL_APP = 100
        private const val RC_OVERLAY_PERMISSION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        successCheckPermissions {
            initWizardFragment()
        }
    }

    override fun onBackPressed() {
        val currentFragment = GuidedStepSupportFragment.getCurrentGuidedStepSupportFragment(supportFragmentManager)
        if (currentFragment is WizardFragment1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun initWizardFragment() {
        window.setBackgroundDrawableResource(R.drawable.wizard_background_blackned)

        val fragment = WizardFragment1()
        fragment.arguments = intent.extras
        GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
    }

    private fun successCheckPermissions(callbackSuccess: () -> Unit) {
        val listPermissions = ArrayList<String>()

        listPermissions.addPermissionsApp()

        Dexter.withContext(this)
            .withPermissions(listPermissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report != null) {
                        logicOnPermissionChecked(report, callbackSuccess)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?, token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()
    }

    private fun ArrayList<String>.addPermissionsApp() {

        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_NETWORK_STATE)
        add(Manifest.permission.ACCESS_WIFI_STATE)
        add(Manifest.permission.CHANGE_WIFI_STATE)
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.INTERNET)
        add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
        add(Manifest.permission.RECORD_AUDIO)
        add(Manifest.permission.WAKE_LOCK)
        add(Manifest.permission.SET_ALARM)
        add(Manifest.permission.VIBRATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.READ_PHONE_STATE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            add(Manifest.permission.CHANGE_NETWORK_STATE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            add(Manifest.permission.FOREGROUND_SERVICE)
        }

        if (MyBaseApplication.isTVBox) {
            add(Manifest.permission.BLUETOOTH)
            add(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    private fun logicOnPermissionChecked(
        report: MultiplePermissionsReport,
        callbackSuccess: () -> Unit
    ) {
        if (report.areAllPermissionsGranted()) {

            hasPermissions = (checkPermissionUnknownAppSources() &&
                    checkPermissionOverlay())

            if (hasPermissions) {
                callbackSuccess()
            }

        } else {
            hasPermissions = false
            toast(R.string.message_permission_required, type = TypeToasty.ERROR)
            finishAffinity()
            finish()
        }
    }

    private fun checkPermissionUnknownAppSources(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (!packageManager.canRequestPackageInstalls()) {
                val uri = Uri.parse("package:$packageName")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                intent.data = uri

                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, RC_INSTALL_APP)
                } else {
                    toast(R.string.err_permissions_unknown_app_sources, type = TypeToasty.ERROR)
                }

                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun checkPermissionOverlay(): Boolean {
        val requestPermission = !MyBaseApplication.isTVBox || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestPermission &&
            !Settings.canDrawOverlays(this)
        ) {

            var intent = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            } else {
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, RC_OVERLAY_PERMISSION)
            } else {

                intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, RC_OVERLAY_PERMISSION)
                } else {
                    toast(R.string.err_permissions_overlay, type = TypeToasty.ERROR)
                }
            }

            false
        } else {
            true
        }
    }

}