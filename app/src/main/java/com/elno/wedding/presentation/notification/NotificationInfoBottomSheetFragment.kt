package com.elno.wedding.presentation.notification

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.elno.wedding.common.UtilityFunctions
import com.elno.wedding.common.UtilityFunctions.getLocalizedTextFromMap
import com.elno.wedding.databinding.NotificationInfoBottomSheetBinding
import com.elno.wedding.domain.model.NotificationModel
import com.elno.wedding.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationInfoBottomSheetFragment(
    private val notificationModel: NotificationModel?,
    private val onClick: (action: String?, id: String?) -> Unit
) : BaseDialogFragment<NotificationInfoBottomSheetBinding>(NotificationInfoBottomSheetBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.setOnClickListener {
            dismiss()
        }
        binding.container.setOnClickListener(null)
        binding.close.setOnClickListener {
            dismiss()
        }

        binding.title.text = getLocalizedTextFromMap(context, notificationModel?.title)
        binding.description.text = getLocalizedTextFromMap(context, notificationModel?.subtitle)
        notificationModel?.time?.let { binding.time.text = UtilityFunctions.convertDate(context, it) }
        binding.actionButton.isVisible = (notificationModel?.action != NotificationAction.INFO.value)
        binding.actionButton.setOnClickListener {
            onClick(notificationModel?.action, notificationModel?.actionId)
            dismiss()
        }
    }

}