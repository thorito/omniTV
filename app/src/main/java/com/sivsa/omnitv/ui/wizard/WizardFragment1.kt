package com.sivsa.omnitv.ui.wizard

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sivsa.omnitv.R
import com.sivsa.omnitv.tools.toast

class WizardFragment1: GuidedStepSupportFragment() {

    companion object {
        private const val ID_TITLE = 1L
        private const val ID_BUTTON = 2L
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {

        val title = getString(R.string.title_name)
        val description = getString(R.string.title_description)
        val step = getString(R.string.step1)
        val icon = ContextCompat.getDrawable(requireContext(), R.drawable.face_01)

        return GuidanceStylist.Guidance(title, description, step, icon)
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        super.onCreateActions(actions, savedInstanceState)
        actions.add(GuidedAction.Builder(requireContext())
                .id(ID_TITLE)
                .title(R.string.action_name)
                .description(R.string.action_required)
                .editDescription("hola")
                .checked(true)
                .clickAction(ID_TITLE)
                /*.descriptionEditable(true)
                .descriptionInputType(InputType.TYPE_CLASS_TEXT)*/
                //.descriptionEditInputType(InputType.TYPE_CLASS_TEXT)
                .build()
        )

    }

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        super.onCreateButtonActions(actions, savedInstanceState)
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_BUTTON)
            .title("ACEPTAR")
            .build())
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        context?.apply {

            when (action?.id) {
                ID_TITLE -> toast("Click ID_TITLE")
                ID_BUTTON -> toast("Click ID_BUTTON")
                else -> toast("Click desconocido")
            }
        }

        super.onGuidedActionClicked(action)
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction?): Long {

        context?.apply {

            when (action?.id) {
                ID_TITLE -> toast("Editado ID_TITLE")
                ID_BUTTON -> toast("Editado ID_BUTTON")
                else -> toast("Editado desconocido")
            }
        }

        return super.onGuidedActionEditedAndProceed(action)
    }

}