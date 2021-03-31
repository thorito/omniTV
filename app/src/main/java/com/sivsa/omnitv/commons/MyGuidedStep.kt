package com.sivsa.omnitv.commons

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidedAction
import com.sivsa.omnitv.tools.setLeftPanelGuidedStep

open class MyGuidedStep: GuidedStepSupportFragment() {

    private var container: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view.findViewById(androidx.leanback.R.id.content_fragment)
        container?.setLeftPanelGuidedStep(resources.configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        container?.setLeftPanelGuidedStep(newConfig)
    }

    fun getAction(id: Long): Pair<GuidedAction, Int> =
        if (MyBaseApplication.isTVBox) {
            Pair(findButtonActionById(id), findButtonActionPositionById(id))
        } else {
            Pair(findActionById(id),  findActionPositionById(id))
        }
}