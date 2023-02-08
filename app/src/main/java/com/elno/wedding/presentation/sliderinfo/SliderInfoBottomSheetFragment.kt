package com.elno.wedding.presentation.sliderinfo

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elno.wedding.common.UtilityFunctions.getLocaleText
import com.elno.wedding.databinding.FragmentSliderInfoBottomSheetBinding
import com.elno.wedding.domain.model.SliderModel
import com.elno.wedding.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SliderInfoBottomSheetFragment(private val sliderModel: SliderModel?) : BaseDialogFragment<FragmentSliderInfoBottomSheetBinding>(FragmentSliderInfoBottomSheetBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.setOnClickListener {
            dismiss()
        }

        context?.let { ctx ->
            sliderModel?.let { model ->
                Glide.with(ctx)
                    .load(model.image)
                    .into(binding.image)
                binding.title.text = getLocaleText(ctx, model.title)
                binding.description.text = getLocaleText(ctx, model.description)
            }
        }
    }
}