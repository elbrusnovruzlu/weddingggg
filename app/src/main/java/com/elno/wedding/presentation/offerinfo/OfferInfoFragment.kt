package com.elno.wedding.presentation.offerinfo

import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.elno.wedding.R
import com.elno.wedding.common.Constants.OFFER_MODEL
import com.elno.wedding.common.UtilityFunctions.dpToPx
import com.elno.wedding.data.local.LocalDataStore
import com.elno.wedding.databinding.FragmentOfferInfoBinding
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.RouteInfoAdapter
import com.elno.wedding.presentation.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OfferInfoFragment : BaseFragment<FragmentOfferInfoBinding>(FragmentOfferInfoBinding::inflate) {

    var vendorModel: VendorModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vendorModel= arguments?.getParcelable(OFFER_MODEL)
    }


    override fun setupViews() {
        setButtonMargin()
        vendorModel?.imageUrl?.let {
            Glide.with(requireContext())
                .load(it)
                .into(binding.imageView)
        }
        binding.favButton.isChecked = LocalDataStore(context).getList().contains(vendorModel) == true
        binding.name.text = vendorModel?.title
        binding.type.text = vendorModel?.type?.replaceFirstChar(Char::titlecase)
        binding.price.text = getString(R.string.price_starts_at, vendorModel?.minPrice.toString())

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = RouteInfoAdapter(this, vendorModel)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Detail"
                1 -> "Gallery"
                else -> "Contact"
            }
        }.attach()

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab: View = ((binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)) as View
            val p = tab.layoutParams as MarginLayoutParams
            p.setMargins(0, 0, dpToPx(requireContext(), 8), 0)
            tab.requestLayout()
        }

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.callButton.isVisible = tab.position == 0
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setButtonMargin() {
        if (isEdgeToEdgeEnabled() != 2) {
            val layoutParams = binding.callButton.layoutParams as MarginLayoutParams
            layoutParams.updateMargins(
                bottom = dpToPx(requireContext(), 64)
            )
        }
    }

    override fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.favButton.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                LocalDataStore(context).addToList(vendorModel)
            }
            else {
                LocalDataStore(context).removeFromList(vendorModel)
            }
        }
    }

    private fun isEdgeToEdgeEnabled(): Int {
        val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        return if (resourceId > 0) {
            resources.getInteger(resourceId)
        } else 0
    }

}