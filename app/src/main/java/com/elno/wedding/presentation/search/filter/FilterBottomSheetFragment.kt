package com.elno.wedding.presentation.search.filter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.elno.wedding.R
import com.elno.wedding.common.UtilityFunctions
import com.elno.wedding.common.UtilityFunctions.getIdFromMap
import com.elno.wedding.databinding.FilterPriceViewBinding
import com.elno.wedding.databinding.FilterTitleViewBinding
import com.elno.wedding.databinding.FragmentFilterBottomSheetBinding
import com.elno.wedding.domain.model.FilterModel
import com.elno.wedding.domain.model.FilteringModel
import com.elno.wedding.presentation.base.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FilterBottomSheetFragment(
    private val filteringModel: FilteringModel,
    private val filter: ArrayList<FilterModel>?,
    private val onClick: (filteringModel: FilteringModel) -> Unit
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

        context?.let { ctx ->
            binding.filterContainer.removeAllViews()
            filter?.forEachIndexed { index, it ->
                val titleView = FilterTitleViewBinding.inflate(LayoutInflater.from(context), binding.rootView, false)
                titleView.titleTextView.text = UtilityFunctions.getLocalizedTextFromMap(ctx, it.title)
                titleView.root.id = index*2
                binding.filterContainer.addView(titleView.root)
                if(it.type == "price") {
                    val priceView = FilterPriceViewBinding.inflate(LayoutInflater.from(context), binding.rootView, false)
                    priceView.minPrice.label.text = getString(R.string.min_price)
                    priceView.maxPrice.label.text = getString(R.string.max_price)
                    priceView.minPrice.value.setText(getTextValue(filteringModel.minPrice))
                    priceView.maxPrice.value.setText(getTextValue(filteringModel.maxPrice))
                    priceView.minPrice.value.tag = "minPrice"
                    priceView.maxPrice.value.tag = "maxPrice"
                    priceView.root.id = index*2 + 1
                    binding.filterContainer.addView(priceView.root)
                } else if(it.type == "choice") {
                    binding.checkBoxLayout.isVisible = true
                    val linearLayout = LinearLayout(ctx)
                    linearLayout.orientation = LinearLayout.VERTICAL
                    linearLayout.tag = "choice"
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.topMargin = UtilityFunctions.dpToPx(ctx, 4)
                    linearLayout.layoutParams = params
                    linearLayout.id = index*2 + 1
                    it.choices?.forEachIndexed { choiceIndex, choice ->
                        val checkBox = getCheckBoxLayout(UtilityFunctions.getLocalizedTextFromMap(ctx, choice), choiceIndex, ctx)
                        val id = getIdFromMap(choice)
                        checkBox.tag = id
                        checkBox.isChecked = filteringModel.filterMap?.get(it.name)?.contains(id) == true
                        linearLayout.addView(checkBox)
                    }
                    binding.filterContainer.addView(linearLayout)
                }
            }
        }





        binding.filterBtn.setOnClickListener {
            val minPrice = binding.filterContainer.findViewWithTag<TextView>("minPrice")
            val maxPrice = binding.filterContainer.findViewWithTag<TextView>("maxPrice")
            filteringModel.minPrice = getLongValue(minPrice.text.toString())
            filteringModel.maxPrice = getLongValue(maxPrice.text.toString())

            val hashMap = hashMapOf<String, ArrayList<String>>()
            binding.filterContainer.children.forEachIndexed { index, view ->
                if(view.tag == "choice") {
                    val arrayList = ArrayList<String>()
                    (view as? LinearLayout)?.children?.forEach {
                        val checkBox = it as? CheckBox
                        if(checkBox?.isChecked == true) {
                            arrayList.add(checkBox.tag as String)
                        }
                    }
                    filter?.getOrNull((index-1)/2)?.name?.let { choiceName ->
                        if(arrayList.isNotEmpty()) hashMap[choiceName] = arrayList
                    }
                }
            }
            filteringModel.filterMap = hashMap
            onClick(filteringModel)
            dismiss()
        }
        binding.clearFilterBtn.setOnClickListener {
            onClick(FilteringModel())
            dismiss()
        }
    }

    private fun getTextValue(price: Long): String {
        return if(price == 0L) "" else price.toString()
    }

    private fun getLongValue(price: String): Long {
        return if(price == "") 0 else price.toLong()
    }


    private fun getCheckBoxLayout(value: String?, index: Int, ctx: Context): CheckBox {
        val checkBox = CheckBox(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        checkBox.layoutParams = params
        checkBox.setPadding(0, UtilityFunctions.dpToPx(ctx,4), 0, UtilityFunctions.dpToPx(ctx,4))
        checkBox.id = index
        checkBox.text = value
        checkBox.gravity = Gravity.CENTER_VERTICAL
        context?.let {
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ), intArrayOf(
                    ContextCompat.getColor(it, R.color.asset_disabled),  // disabled
                    ContextCompat.getColor(it, R.color.brandColor) // enabled
                )
            )
            checkBox.buttonTintList = colorStateList
        }

        TextViewCompat.setTextAppearance(checkBox, R.style.sub_headline_regular_black)

        return checkBox
    }


}