package com.elno.wedding.presentation.notification

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.elno.wedding.databinding.FragmentPrivacyPolicyBottomSheetBinding
import com.elno.wedding.databinding.NotificationInfoBottomSheetBinding
import com.elno.wedding.domain.model.NotificationModel
import com.elno.wedding.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationInfoBottomSheetFragment(
    private val notificationModel: NotificationModel?,
    private val onClick: (vendorId: String?) -> Unit
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
        binding.title.text = notificationModel?.title
        binding.description.text = notificationModel?.description
        binding.actionButton.isVisible = (notificationModel?.action == NotificationAction.OPEN_VENDOR.value)
        notificationModel?.imageUrl?.let {
            Glide.with(requireContext())
                .load(it)
                .into(binding.imageView)
        }
        binding.actionButton.setOnClickListener {
            onClick(notificationModel?.vendorId)
            dismiss()
        }
    }

}