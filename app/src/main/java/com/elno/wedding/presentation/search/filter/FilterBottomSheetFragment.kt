package com.elno.wedding.presentation.search.filter

import android.os.Bundle
import android.view.View
import android.view.View.NO_ID
import com.elno.wedding.R
import com.elno.wedding.common.Static.filterModel
import com.elno.wedding.common.UtilityFunctions.getLocalizedTextFromMap
import com.elno.wedding.databinding.FragmentFilterBottomSheetBinding
import com.elno.wedding.domain.model.CategoryModel
import com.elno.wedding.presentation.base.BaseDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheetFragment(
    private var categoryType: String,
    private val minPrice: Long,
    private val maxPrice: Long,
    private val onClick: (categoryType: String, minPrice: Long, maxPrice: Long) -> Unit
    ) : BaseDialogFragment<FragmentFilterBottomSheetBinding>(FragmentFilterBottomSheetBinding::inflate) {

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

        binding.priceSlider.setValues(minPrice.toFloat(), maxPrice.toFloat())
        binding.priceSlider.valueTo = filterModel.maxPrice.toFloat()
        binding.minPrice.value.text = "$minPrice ₼"
        binding.maxPrice.value.text = "$maxPrice ₼"

        filterModel.categories?.forEach {
            addToChipGroup(it)
        }

        binding.filterBtn.setOnClickListener {
            val chipId = binding.chipGroup.checkedChipId
            val type = if(chipId == NO_ID) {
                "all"
            } else {
                val chip: Chip = binding.chipGroup.findViewById(chipId)
                chip.tag as String
            }
            onClick(type, binding.priceSlider.values[0].toLong(), binding.priceSlider.values[1].toLong())
            dismiss()
        }
        binding.clearFilterBtn.setOnClickListener {
            onClick("all", 0, filterModel.maxPrice)
            dismiss()
        }
    }

    private fun addToChipGroup(categoryModel: CategoryModel?) {
        val chip = layoutInflater.inflate(R.layout.chip_item, binding.chipGroup, false) as Chip
        chip.text = getLocalizedTextFromMap(context, categoryModel?.name)
        chip.isChecked = categoryType == categoryModel?.type
        chip.tag = categoryModel?.type
        binding.chipGroup.addView(chip)
    }

}