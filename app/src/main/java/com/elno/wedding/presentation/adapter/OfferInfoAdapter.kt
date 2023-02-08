package com.elno.wedding.presentation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elno.wedding.common.Constants.OFFER_MODEL
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.offerinfo.contact.ContactFragment
import com.elno.wedding.presentation.offerinfo.detail.OfferDetailFragment
import com.elno.wedding.presentation.offerinfo.galllery.OfferGalleryFragment

class OfferInfoAdapter(fragment: Fragment, private val vendorModel: VendorModel?): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> OfferDetailFragment()
            1 -> OfferGalleryFragment()
            2 -> ContactFragment()
            else -> throw IllegalStateException()
        }
        fragment.arguments = Bundle().apply {
            putParcelable(OFFER_MODEL, vendorModel)
        }

        return fragment
    }
}