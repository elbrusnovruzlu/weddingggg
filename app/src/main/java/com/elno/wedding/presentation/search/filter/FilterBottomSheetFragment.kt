package com.elno.wedding.presentation.search.filter

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.elno.wedding.R
import com.elno.wedding.databinding.FragmentFilterBottomSheetBinding
import com.elno.wedding.databinding.FragmentSliderInfoBottomSheetBinding
import com.elno.wedding.domain.model.SliderModel
import com.elno.wedding.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheetFragment(
    private val categoryType: String,
    private val minPrice: Float,
    private val maxPrice: Float,
    private val filterMaxPrice: Float,
    private val onClick: (categoryType: String, minPrice: Float, maxPrice: Float) -> Unit
    ) : BaseDialogFragment<FragmentFilterBottomSheetBinding>(FragmentFilterBottomSheetBinding::inflate) {

    private var type = Category.ALL.value

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.setOnClickListener {
            dismiss()
        }
        binding.container.setOnClickListener(null)
        binding.close.setOnClickListener {
            dismiss()
        }

        binding.minPrice.label.text = getString(R.string.min_price)
        binding.maxPrice.label.text = getString(R.string.max_price)

        binding.priceSlider.addOnChangeListener { rangeSlider, value, fromUser ->
            val rangeValues = rangeSlider.values
            binding.minPrice.value.text = "${rangeValues[0].toInt()} ₼"
            binding.maxPrice.value.text = "${rangeValues[1].toInt()} ₼"
        }

        binding.priceSlider.setValues(minPrice, maxPrice)
        binding.priceSlider.valueTo = filterMaxPrice
        binding.minPrice.value.text = "${minPrice.toInt()} ₼"
        binding.maxPrice.value.text = "${maxPrice.toInt()} ₼"

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            type = when(checkedId) {
                R.id.decorationRadioButton -> Category.DECORATION.value
                R.id.photographyRadioButton -> Category.PHOTOGRAPHY.value
                R.id.showRadioButton -> Category.SHOW.value
                else -> Category.ALL.value
            }
        }

        when (categoryType) {
            Category.DECORATION.value -> binding.decorationRadioButton.isChecked = true
            Category.PHOTOGRAPHY.value -> binding.photographyRadioButton.isChecked = true
            Category.SHOW.value -> binding.showRadioButton.isChecked = true
        }

        binding.filterBtn.setOnClickListener {
            onClick(type, binding.priceSlider.values[0], binding.priceSlider.values[1])
            dismiss()
        }
        binding.clearFilterBtn.setOnClickListener {
            onClick(Category.ALL.value, 0f, filterMaxPrice)
            dismiss()
        }

    }

}