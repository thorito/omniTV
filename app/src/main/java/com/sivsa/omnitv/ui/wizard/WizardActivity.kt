package com.sivsa.omnitv.ui.wizard

import android.Manifest
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sivsa.omnitv.R
import com.sivsa.omnitv.models.User
import com.sivsa.omnitv.tools.ToolsImage
import com.sivsa.omnitv.tools.TypeToasty
import com.sivsa.omnitv.tools.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class WizardActivity : FragmentActivity() {

    private var hasPermissions: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pb.visibility = View.VISIBLE

        successCheckPermissions {
            initWizardFragmentUserName()
        }
    }

    override fun onBackPressed() {
        val currentFragment = GuidedStepSupportFragment.getCurrentGuidedStepSupportFragment(supportFragmentManager)
        if (currentFragment is WizardFragmentUserName) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun initWizardFragmentUserName() {

        lifecycleScope.launch(Dispatchers.Main) {

            window.setBackgroundDrawableResource(R.drawable.bg_provisioning)

            val user = User(
                    name = "Pepito Grillo",
                    login = "pepito@gmail.com",
//                  thumbPhoto = "https://interactive-examples.mdn.mozilla.net/media/cc0-images/grapefruit-slice-332-332.jpg",
                    thumbPhoto = "https://lh3.googleusercontent.com/a-/AOh14Gjz7-phqs6gPAdPitE7nfhflsqtkqTj6kvDfiPeHA=s96-c-rg-br100",
                    provisioning = true)

            val currentStep = 1
            val totalSteps = getTotalStep(user)
            val icon = ToolsImage(this@WizardActivity)
                .getDrawableIcon(iconDefault = R.drawable.ic_user, user.thumbPhoto)
            val fragment = WizardFragmentUserName(user, icon, currentStep, totalSteps)
            GuidedStepSupportFragment.addAsRoot(this@WizardActivity, fragment, android.R.id.content)

        }
    }

    private fun getTotalStep(user: User): Int {

        // Simulo el número total de pasos (si fuese nuevo usuario -> +1)
        return if (user.provisioning) {
            4
        } else {
            3
        }
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
            pb.visibility = View.GONE
            callbackSuccess()
        } else {
            hasPermissions = false
            toast(R.string.message_permission_required, type = TypeToasty.ERROR)
            finishAffinity()
            finish()
        }
    }
}