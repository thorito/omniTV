package com.sivsa.omnitv.ui.wizard

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.sivsa.omnitv.R
import com.sivsa.omnitv.models.User
import com.sivsa.omnitv.tools.TypeToasty
import com.sivsa.omnitv.tools.toast

class WizardFragmentChecks(
    private val user: User,
    private val icon: Drawable?,
    private val currentStep: Int,
    private val totalSteps: Int) : GuidedStepSupportFragment() {

    companion object {
        private const val ID_REMINDERS = 1L
        private const val ID_SUBTITLES = 2L
        private const val ID_LSE = 3L
        private const val ID_ALBUM = 4L
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        val title = getString(R.string.title_settigs)
        val description = getString(R.string.title_description_settings)
        val step = getString(R.string.step, currentStep, totalSteps)
        return GuidanceStylist.Guidance(title, description, step, icon)
    }


    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {

        super.onCreateActions(actions, savedInstanceState)

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_REMINDERS)
                .title(R.string.check_subtitles)
                .description(R.string.description_reminders)
                .checkSetId(GuidedAction.DEFAULT_CHECK_SET_ID)
                .checked(false)
                .build()
        )

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_SUBTITLES)
                .title(R.string.check_subtiles)
                .description(R.string.description_subtitles)
                .checkSetId(GuidedAction.DEFAULT_CHECK_SET_ID)
                .checked(false)
                .build()
        )

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_LSE)
                .title(R.string.check_lse)
                .description(R.string.description_lse)
                .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                .checked(false)
                .build()
        )

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ID_ALBUM)
                .title(R.string.check_album)
                .description(R.string.description_album)
                .checkSetId(GuidedAction.CHECKBOX_CHECK_SET_ID)
                .checked(false)
                .build()
        )
    }

    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        super.onCreateButtonActions(actions, savedInstanceState)
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(GuidedAction.ACTION_ID_FINISH)
                .title(R.string.action_finish)
                .enabled(true)
                .build()
        )

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(GuidedAction.ACTION_ID_CANCEL)
                .title(R.string.action_cancelar)
                .enabled(true)
                .build()
        )
    }


    override fun onGuidedActionClicked(action: GuidedAction?) {

        when (action?.id) {
            GuidedAction.ACTION_ID_FINISH -> {
                toast(R.string.registration_completed, type = TypeToasty.SUCCESS)
                finishGuidedStepSupportFragments()
            }
            GuidedAction.ACTION_ID_CANCEL -> fragmentManager?.popBackStack()
            else -> super.onGuidedActionClicked(action)
        }
    }
}