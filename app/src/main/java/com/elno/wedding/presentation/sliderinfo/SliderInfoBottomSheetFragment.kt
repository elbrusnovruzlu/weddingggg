package com.elno.wedding.presentation.sliderinfo

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elno.wedding.common.UtilityFunctions.getLocalizedTextFromMap
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
                    .load(getLocalizedTextFromMap(ctx, model.image))
                    .into(binding.image)
                binding.title.text = getLocalizedTextFromMap(ctx, model.title)
                binding.description.text = getLocalizedTextFromMap(ctx, model.description)
            }
        }
    }
}