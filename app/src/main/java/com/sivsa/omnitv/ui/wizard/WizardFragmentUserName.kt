package com.sivsa.omnitv.ui.wizard

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import androidx.lifecycle.lifecycleScope
import com.sivsa.omnitv.R
import com.sivsa.omnitv.commons.MyBaseApplication
import com.sivsa.omnitv.commons.MyGuidedStep
import com.sivsa.omnitv.models.User
import com.sivsa.omnitv.tools.ToolsImage
import com.sivsa.omnitv.tools.TypeToasty
import com.sivsa.omnitv.tools.afterTextChanged
import com.sivsa.omnitv.tools.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WizardFragmentUserName(
        private val user: User,
        private val icon: Drawable?,
        private val currentStep: Int,
        private val totalSteps: Int) : MyGuidedStep() {

    companion object {
        private const val ID = 1L
    }

    private lateinit var etTitle: EditText
    private lateinit var etEdit: EditText
    private var editTemporal: String? = null

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        val title = getString(R.string.title_name)
        val description = getString(R.string.title_description_name)
        val step = getString(R.string.step, currentStep, totalSteps)

        return GuidanceStylist.Guidance(title, description, step, icon)
    }

    override fun onCreateActionsStylist(): GuidedActionsStylist {
        return object : GuidedActionsStylist() {
            override fun setupImeOptions(
                    vh: ViewHolder,
                    action: GuidedAction
            ) {

                if (action.id == ID) {
                    etTitle = vh.editableTitleView
                    etEdit = vh.editableDescriptionView

                    editTemporal = etEdit.text?.toString()

                    etEdit.afterTextChanged {
                        editTemporal = it
                    }
                    refreshButtonNext(GuidedAction.ACTION_ID_NEXT)
                } else {
                    super.setupImeOptions(vh, action)
                }
            }
        }
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        super.onCreateActions(actions, savedInstanceState)

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID)
                .icon(R.drawable.ic_edit)
                .title(R.string.action_name)
                .description(user.name)
                .descriptionEditable(true)
                .descriptionInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .descriptionEditInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .build()
        )

        if (!MyBaseApplication.isTVBox) {

            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(GuidedAction.ACTION_ID_NEXT)
                    .title(R.string.action_next)
                    .enabled(false)
                    .clickAction(GuidedAction.ACTION_ID_NEXT)
                    .build()
            )

            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(GuidedAction.ACTION_ID_CANCEL)
                    .title(R.string.action_cancel)
                    .enabled(true)
                    .clickAction(GuidedAction.ACTION_ID_CANCEL)
                    .build()
            )
        }

    }

    override fun onGuidedActionFocused(action: GuidedAction?) {
        super.onGuidedActionFocused(action)
        etEdit.setSelection(0, etEdit.text.length)
    }

    override fun onCreateButtonActions(
            actions: MutableList<GuidedAction>,
            savedInstanceState: Bundle?
    ) {
        super.onCreateButtonActions(actions, savedInstanceState)

        if (MyBaseApplication.isTVBox) {
            actions.add(
                    GuidedAction.Builder(requireContext())
                            .id(GuidedAction.ACTION_ID_NEXT)
                            .title(R.string.action_next)
                            .enabled(false)
                            .build()
            )

            actions.add(
                    GuidedAction.Builder(requireContext())
                            .id(GuidedAction.ACTION_ID_CANCEL)
                            .title(R.string.action_cancel)
                            .enabled(true)
                            .build()
            )
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction?): Long {

        if (action?.id == ID) {
            refreshButtonNext(GuidedAction.ACTION_ID_NEXT)
        }

        return super.onGuidedActionEditedAndProceed(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        when (action?.id) {
            GuidedAction.ACTION_ID_NEXT -> {
                lifecycleScope.launch {

                    if (validateFields()) {
                        user.name = findActionById(ID).description.toString()
                        val fragment = if (user.provisioning) {
                            val icon = ToolsImage(requireContext())
                                    .getDrawableIcon(iconDefault = R.drawable.ic_password,
                                            urlImage = "https://static.wikia.nocookie.net/videojuego/images/e/e1/Contrase%C3%B1a.jpg/revision/latest?cb=20190801205345")
                            WizardFragmentPasswordUser(user, icon, currentStep + 1, totalSteps)
                        } else {
                            val icon = ToolsImage(requireContext())
                                    .getDrawableIcon(iconDefault = R.drawable.ic_devices,
                                            urlImage = "https://3.bp.blogspot.com/-faVv5x8Ujwg/XLnlkQ3-smI/AAAAAAAAA-U/oyJVT1rilKwPMHLrvdSiEmkRAtSvTOm6ACLcBGAs/s1600/MECOOL%2BKM3.jpg")

                            WizardFragmentAlias(user, icon, currentStep + 1, totalSteps)
                        }
                        add(fragmentManager, fragment)
                    }
                }

            }
            GuidedAction.ACTION_ID_CANCEL -> finishGuidedStepSupportFragments()
            else -> super.onGuidedActionClicked(action)
        }
    }

    private fun validateFields(): Boolean {
        val action = findActionById(ID)

        val name = action?.description
        return if (name.isNullOrBlank() || editTemporal.isNullOrBlank()) {
            val messageError = getString(R.string.err_required_name)
            etTitle.error = messageError
            toast(messageError, type = TypeToasty.ERROR)
            false
        } else {
            etTitle.error = null
            true
        }
    }

    private fun refreshButtonNext(id: Long) {

        val (guidedAction, pos) = getAction(id)
        guidedAction.apply {
            isEnabled = validateFields()

            lifecycleScope.launch {
                delay(500)
                if (MyBaseApplication.isTVBox) {
                    notifyButtonActionChanged(pos)
                } else {
                    notifyActionChanged(pos)
                }
            }
        }
    }
}