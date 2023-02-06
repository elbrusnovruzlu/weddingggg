package com.elno.wedding.presentation.offerinfo.galllery

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.elno.wedding.common.Constants
import com.elno.wedding.databinding.FragmentOfferGalleryBinding
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.GalleryAdapter
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OfferGalleryFragment : BaseFragment<FragmentOfferGalleryBinding>(FragmentOfferGalleryBinding::inflate) {

    var vendorModel: VendorModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendorModel= arguments?.getParcelable(Constants.OFFER_MODEL)
    }

    override fun setupViews() {
        val adapter = GalleryAdapter {
            onImageClick(it)
        }.apply {
            submitList(vendorModel?.images)
        }
        binding.gridView.adapter = adapter
        binding.gridView.layoutManager = GridLayoutManager(context, 2)
    }

    private fun onImageClick(position: Int) {
        GallerySliderFullScreenDialog(vendorModel?.images, position).show(parentFragmentManager, GallerySliderFullScreenDialog::class.java.canonicalName)
    }
}