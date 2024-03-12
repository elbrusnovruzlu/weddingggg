package com.elno.wedding.presentation.search

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.common.Resource
import com.elno.wedding.common.UtilityFunctions
import com.elno.wedding.databinding.FragmentSearchBinding
import com.elno.wedding.domain.model.FilteringModel
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.LoadingAdapter
import com.elno.wedding.presentation.adapter.VendorAdapter
import com.elno.wedding.presentation.base.BaseFragment
import com.elno.wedding.presentation.custom.EndlessRecyclerViewScrollListener
import com.elno.wedding.presentation.search.filter.FilterBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate), SearchView.OnQueryTextListener {

    private val viewModel: SearchViewModel by viewModels()

    private var adapter: VendorAdapter? = VendorAdapter(
        { onOfferClick(it) },
        { onEmptyResult(it) },
    )
    private val loadingAdapter = LoadingAdapter()
    private lateinit var baseAdapter: ConcatAdapter

    private val recyclerViewListener = object : EndlessRecyclerViewScrollListener() {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
            baseAdapter.addAdapter(loadingAdapter)
            viewModel.searchVendorList(true)
        }
    }

    override fun setupViews() {
        binding.priceChip.isCloseIconVisible = false
        viewModel.categoryType = arguments?.getString(Constants.CATEGORY_TYPE)
        baseAdapter = ConcatAdapter(adapter)
        binding.gridView.adapter = baseAdapter
        binding.gridView.layoutManager = GridLayoutManager(context, 2)
        binding.gridView.addOnScrollListener(recyclerViewListener)
        binding.title.text = UtilityFunctions.getType(context, viewModel.categoryType)
        viewModel.getFilter()
        viewModel.searchVendorList()
    }

    private fun onEmptyResult(isEmpty: Boolean) {
        binding.emptyLayout.isVisible = isEmpty
    }

    override fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.priceChip.setOnClickListener {
            openFilterDialog()
        }
        binding.priceChip.setOnCloseIconClickListener {
            viewModel.filteringModel = FilteringModel()
            viewModel.lastIndex = 0
            recyclerViewListener.resetState()
            setFilterIcon()
            binding.emptyLayout.isVisible = false
            viewModel.searchVendorList()
        }
        binding.searchView.setOnQueryTextListener(this)
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
                binding.emptyLayout.isVisible = resource.data.isNullOrEmpty()
                binding.searchView.setOnQueryTextListener(null)
                binding.searchView.setQuery("", false)
                binding.searchView.clearFocus()
                binding.searchView.setOnQueryTextListener(this)
                adapter?.submitList(resource.data?: arrayListOf())
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

    private fun consumeMoreVendorListResult(resource: Resource<ArrayList<VendorModel?>>) {
        when(resource) {
            is  Resource.Success -> {
                baseAdapter.removeAdapter(loadingAdapter)
                adapter?.addList(resource.data?: mutableListOf())
            }
            is  Resource.Error -> {
                baseAdapter.removeAdapter(loadingAdapter)
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
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
        val dialog = FilterBottomSheetFragment(viewModel.filteringModel, viewModel.filterResult) { filteringModel ->
            onFilterResult(filteringModel)
        }
        dialog.show(parentFragmentManager, FilterBottomSheetFragment::class.java.canonicalName)
    }

    private fun onFilterResult(filteringModel: FilteringModel) {
        viewModel.filteringModel = filteringModel
        setFilterIcon()
        viewModel.searchQuery = ""
        binding.searchView.setOnQueryTextListener(null)
        binding.searchView.setQuery("", false)
        binding.searchView.setOnQueryTextListener(this)
        binding.emptyLayout.isVisible = false
        recyclerViewListener.resetState()
        viewModel.lastIndex = 0
        viewModel.searchVendorList()
    }

    private fun setFilterIcon() {
        if(viewModel.filteringModel.minPrice != 0L || viewModel.filteringModel.maxPrice != 0L || viewModel.filteringModel.filterMap?.isNotEmpty() == true) {
            binding.priceChip.isCloseIconVisible = true
            binding.priceChip.setChipBackgroundColorResource(R.color.filterColorLight)
            binding.priceChip.setTextColor( ContextCompat.getColor(requireContext(), R.color.filterColor))
        }
        else{
            binding.priceChip.isCloseIconVisible = false
            binding.priceChip.setChipBackgroundColorResource(R.color.brandColorLight)
            binding.priceChip.setTextColor( ContextCompat.getColor(requireContext(), R.color.brandColor))
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.lastIndex = 0
        recyclerViewListener.resetState()
        viewModel.searchQuery = newText.orEmpty()
        viewModel.filteringModel = FilteringModel()
        setFilterIcon()
        binding.emptyLayout.isVisible = false
        viewModel.searchVendorList()
        return false
    }

    override fun onDestroyView() {
        binding.searchView.setOnQueryTextListener(null)
        super.onDestroyView()
    }
}