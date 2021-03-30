package com.sivsa.omnitv.ui.wizard

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sivsa.omnitv.R
import com.sivsa.omnitv.tools.TypeToasty
import com.sivsa.omnitv.tools.toast

class WizardActivity : FragmentActivity() {

    private var hasPermissions: Boolean = false

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
    }

    private fun logicOnPermissionChecked(
        report: MultiplePermissionsReport,
        callbackSuccess: () -> Unit
    ) {
        if (report.areAllPermissionsGranted()) {
            callbackSuccess()
        } else {
            hasPermissions = false
            toast(R.string.message_permission_required, type = TypeToasty.ERROR)
            finishAffinity()
            finish()
        }
    }
}