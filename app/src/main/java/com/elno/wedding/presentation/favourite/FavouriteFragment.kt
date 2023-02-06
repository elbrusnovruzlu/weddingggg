package com.elno.wedding.presentation.favourite

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.databinding.FragmentFavouriteBinding
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.VendorAdapter
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteFragment : BaseFragment<FragmentFavouriteBinding>(FragmentFavouriteBinding::inflate) {

    override fun setupViews() {
        val adapter = VendorAdapter {
            onOfferClick(it)
        }.apply {
            submitList(LocalDataStore(context).getList())
        }

        binding.gridView.adapter = adapter
        binding.gridView.layoutManager = GridLayoutManager(context, 2)
    }

    override fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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