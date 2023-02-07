package com.elno.wedding.presentation.more

import android.os.Bundle
import android.view.View
import com.elno.wedding.databinding.FragmentPrivacyPolicyBottomSheetBinding
import com.elno.wedding.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyPolicyBottomSheetFragment(
    private val title: String,
    private val description: String,
) : BaseDialogFragment<FragmentPrivacyPolicyBottomSheetBinding>(FragmentPrivacyPolicyBottomSheetBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.setOnClickListener {
            dismiss()
        }
        binding.container.setOnClickListener(null)
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.title.text = title
        binding.description.text = description
    }

}