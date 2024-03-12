package com.elno.wedding.presentation.favourite

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.elno.wedding.MainActivity
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.common.Constants.FAVOURITE_LIST
import com.elno.wedding.common.Resource
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.databinding.FragmentFavouriteBinding
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.VendorAdapter
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FavouriteFragment : BaseFragment<FragmentFavouriteBinding>(FragmentFavouriteBinding::inflate) {

    private val viewModel: FavouriteViewModel by viewModels()

    private val adapter = VendorAdapter(
        { onOfferClick(it) },
        {}
    )

    override fun setupViews() {
        binding.gridView.adapter = adapter
        binding.gridView.layoutManager = GridLayoutManager(context, 2)
        viewModel.getFavouriteList()
    }

    override fun setupObservers() {
        viewModel.favouriteListResult.observe(
            viewLifecycleOwner,
            ::consumeFavouriteListResult
        )
    }

    private fun consumeFavouriteListResult(resource: Resource<ArrayList<VendorModel?>>?) {
        when(resource) {
            is  Resource.Loading -> {
                binding.loading.isVisible = true
            }
            is  Resource.Success -> {
                binding.loading.isVisible = false
                val favouriteIds = LocalDataStore(context).getList<String>(FAVOURITE_LIST)
                val filteredList = resource.data?.filter { favouriteIds.contains(it?.id) }?.toMutableList()
                if(filteredList.isNullOrEmpty()) {
                    binding.emptyLayout.isVisible = true
                    binding.gridView.isVisible = false
                }
                else {
                    binding.emptyLayout.isVisible = false
                    binding.gridView.isVisible = true
                    adapter.submitList(ArrayList(filteredList))
                }
            }
            is  Resource.Error -> {
                binding.loading.isVisible = false
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.addBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.searchFragment)
        }
    }

    private fun onOfferClick(model: VendorModel?) {
        findNavController().navigate(
            R.id.offerInfoFragment, bundleOf(
            Constants.OFFER_MODEL to model
        )
        )
    }

}