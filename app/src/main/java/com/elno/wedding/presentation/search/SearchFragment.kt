package com.elno.wedding.presentation.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.common.Resource
import com.elno.wedding.databinding.FragmentSearchBinding
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.VendorAdapter
import com.elno.wedding.presentation.base.BaseFragment
import com.elno.wedding.presentation.search.filter.Category
import com.elno.wedding.presentation.search.filter.FilterBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private var adapter: VendorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
        if(sharedPreferences?.contains(Constants.CATEGORY_TYPE) == true) {
            viewModel.categoryType = sharedPreferences.getString(Constants.CATEGORY_TYPE, Category.ALL.value) ?: Category.ALL.value
            sharedPreferences.edit().remove(Constants.CATEGORY_TYPE).apply()
        }
        viewModel.filterMaxPrice = sharedPreferences?.getLong(Constants.FILTER_MAX_PRICE, 20000)?.toFloat()?:20000f
        viewModel.maxPrice = viewModel.filterMaxPrice
    }

    override fun setupViews() {
        context?.let { ctx ->
            adapter = VendorAdapter(ctx) {
                onOfferClick(it)
            }
            binding.gridView.adapter = adapter
        }
        setFilterIcon(viewModel.categoryType, viewModel.minPrice, viewModel.maxPrice)
        viewModel.getVendorList()
    }

    override fun setupListeners() {
        binding.filter.setOnClickListener {
            openFilterDialog()
        }
        binding.redDot.setOnClickListener {
            openFilterDialog()
        }
        binding.categoryChip.setOnClickListener {
            openFilterDialog()
        }
        binding.priceChip.setOnClickListener {
            openFilterDialog()
        }
        binding.categoryChip.setOnCloseIconClickListener {
            viewModel.categoryType = Category.ALL.value
            setFilterIcon(viewModel.categoryType, viewModel.minPrice, viewModel.maxPrice)
            viewModel.getVendorList()
        }
        binding.priceChip.setOnCloseIconClickListener {
            viewModel.minPrice = 0f
            viewModel.maxPrice = viewModel.filterMaxPrice
            setFilterIcon(viewModel.categoryType, viewModel.minPrice, viewModel.maxPrice)
            viewModel.getVendorList()
        }
    }

    override fun setupObservers() {
        viewModel.vendorListResult.observe(
            viewLifecycleOwner,
            ::consumeVendorListResult
        )
    }

    private fun consumeVendorListResult(resource: Resource<ArrayList<VendorModel?>>) {
        when(resource) {
            is  Resource.Loading -> {
                binding.gridView.isVisible = false
                binding.vendorShimmerView.isVisible = true
                binding.vendorShimmerView.startShimmer()
            }
            is  Resource.Success -> {
                resource.data?.let { adapter?.setData(it) }
                binding.vendorShimmerView.stopShimmer()
                binding.gridView.isVisible = true
                binding.vendorShimmerView.isVisible = false
            }
            is  Resource.Error -> {
                binding.vendorShimmerView.stopShimmer()
                binding.vendorShimmerView.isVisible = false
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onOfferClick(model: VendorModel?) {
        findNavController().navigate(
            R.id.offerInfoFragment, bundleOf(
                Constants.OFFER_MODEL to model
            )
        )
    }

    private fun openFilterDialog() {
        val dialog = FilterBottomSheetFragment(viewModel.categoryType, viewModel.minPrice, viewModel.maxPrice, viewModel.filterMaxPrice) { categoryType, minPrice, maxPrice ->
            onFilterResult(categoryType, minPrice, maxPrice)
        }
        dialog.show(parentFragmentManager, FilterBottomSheetFragment::class.java.canonicalName)
    }

    private fun onFilterResult(categoryType: String, minPrice: Float, maxPrice: Float) {
        setFilterIcon(categoryType, minPrice, maxPrice)
        viewModel.categoryType = categoryType
        viewModel.minPrice = minPrice
        viewModel.maxPrice = maxPrice
        viewModel.getVendorList()
    }

    private fun setFilterIcon(categoryType: String, minPrice: Float, maxPrice: Float) {
        binding.categoryChip.text = categoryType.replaceFirstChar(Char::titlecase)
        if(minPrice != 0f || maxPrice != viewModel.filterMaxPrice) {
            binding.priceChip.isVisible = true
            binding.priceChip.text = "${minPrice.toInt()} ₼ - ${maxPrice.toInt()} ₼"
        }
        else{
            binding.priceChip.isVisible = false
        }
        binding.categoryChip.isCloseIconVisible = categoryType != Category.ALL.value
        binding.redDot.isVisible = categoryType != Category.ALL.value || minPrice != 0f || maxPrice != viewModel.filterMaxPrice
    }
}