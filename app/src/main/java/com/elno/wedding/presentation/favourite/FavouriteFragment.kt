package com.elno.wedding.presentation.favourite

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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
        context?.let { ctx ->
            val adapter = VendorAdapter(ctx) {
                onOfferClick(it)
            }.apply {
                setData(LocalDataStore(ctx).getList())
            }
            binding.gridView.adapter = adapter
        }
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