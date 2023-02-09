package com.elno.wedding.presentation.offerinfo.detail

import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.elno.wedding.common.Constants
import com.elno.wedding.common.UtilityFunctions.getLocalizedTextFromMap
import com.elno.wedding.databinding.FragmentOfferDetailBinding
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OfferDetailFragment : BaseFragment<FragmentOfferDetailBinding>(FragmentOfferDetailBinding::inflate) {

    var vendorModel: VendorModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendorModel= arguments?.getParcelable(Constants.OFFER_MODEL)
    }

    override fun setupViews() {
        getLocalizedTextFromMap(requireContext(), vendorModel?.description)?.let {
            binding.description.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

}