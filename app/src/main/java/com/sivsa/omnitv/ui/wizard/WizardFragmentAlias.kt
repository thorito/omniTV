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

class WizardFragmentAlias(
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
        val title = getString(R.string.title_alias)
        val description = getString(R.string.title_description_alias)
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

        val typeDevice = getString(R.string.type_tvbox)
        val name = user.name
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID)
                .icon(R.drawable.ic_edit)
                .title(R.string.action_alias)
                .description(getString(R.string.type_alias, typeDevice, name))
                .descriptionEditable(true)
                .descriptionInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .descriptionEditInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
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
                    .title(R.string.action_back)
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
                    .title(R.string.action_back)
                    .enabled(true)
                    .build()
            )
        }
    }

    override fun onGuidedActionEditedAndProceed(action: GuidedAction?): Long {
        val result = super.onGuidedActionEditedAndProceed(action)

        if (action?.id == ID) {
            refreshButtonNext(GuidedAction.ACTION_ID_NEXT)
        }

        return result
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {

        when (action?.id) {

            GuidedAction.ACTION_ID_NEXT -> {
                lifecycleScope.launch {

                    if (validateFields() || editTemporal.isNullOrBlank()) {

                        val icon = ToolsImage(requireContext())
                                .getDrawableIcon(iconDefault = R.drawable.ic_settings)

                        add(fragmentManager, WizardFragmentChecks(user, icon, currentStep + 1, totalSteps))
                    }
                }

            }
            GuidedAction.ACTION_ID_CANCEL -> fragmentManager?.popBackStack()
            else -> super.onGuidedActionClicked(action)
        }
    }


    private fun validateFields(): Boolean {
        val action = findActionById(ID)

        val alias = action?.description
        return if (alias.isNullOrBlank() || editTemporal.isNullOrBlank()) {
            val messageError = getString(R.string.err_required_alias)
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