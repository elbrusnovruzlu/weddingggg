package com.elno.wedding.presentation.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elno.wedding.MainActivity
import com.elno.wedding.R
import com.elno.wedding.common.Constants.CATEGORY_TYPE
import com.elno.wedding.common.Constants.FILTER_MAX_PRICE
import com.elno.wedding.common.Constants.OFFER_MODEL
import com.elno.wedding.common.Resource
import com.elno.wedding.common.UtilityFunctions.dpToPx
import com.elno.wedding.common.UtilityFunctions.getScreenWidth
import com.elno.wedding.databinding.FragmentDashboardBinding
import com.elno.wedding.domain.model.CategoryModel
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.domain.model.SliderModel
import com.elno.wedding.presentation.adapter.CategoryAdapter
import com.elno.wedding.presentation.adapter.OfferAdapter
import com.elno.wedding.presentation.adapter.SliderAdapter
import com.elno.wedding.presentation.base.BaseFragment
import com.elno.wedding.presentation.search.filter.Category
import com.elno.wedding.presentation.sliderinfo.SliderInfoBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private var viewPagerAdapter: SliderAdapter? = null
    private var offerAdapter: OfferAdapter? = null
    private var categoryAdapter: CategoryAdapter? = null

    private val viewModel: DashboardViewModel by viewModels()

    override fun setupViews() {
        context?.let {

            binding.viewPager.updateLayoutParams<ConstraintLayout.LayoutParams> {
                height = (getScreenWidth() - dpToPx(it, 40))/2
            }

            val categoryList = arrayListOf(
                CategoryModel(R.string.category_decoration, R.drawable.ic_decoration, Category.DECORATION),
                CategoryModel(R.string.category_photograph, R.drawable.ic_photograph, Category.PHOTOGRAPHY),
                CategoryModel(R.string.category_dance_show, R.drawable.ic_dance_show, Category.SHOW),
            )

            viewPagerAdapter = SliderAdapter(it) { model ->
                onSlideClick(model)
            }
            binding.viewPager.adapter = viewPagerAdapter

            offerAdapter = OfferAdapter{ model ->
                onOfferClick(model)
            }
            binding.offersRecyclerView.adapter = offerAdapter

            categoryAdapter = CategoryAdapter { model ->
                onCategoryClick(model)
            }.apply {
                submitList(categoryList)
            }
            binding.categoriesRecyclerView.adapter = categoryAdapter

            viewModel.getSliderList()
            viewModel.getPopularOfferList()

            initLottie()
        }
    }

    override fun setupListeners() {
        binding.favourite.setOnClickListener {
            findNavController().navigate(R.id.favouriteFragment)
        }
        binding.notification.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        )
    }

    private fun initLottie() {
        val sharedPref = activity?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
        val isOnboardShowed = sharedPref?.getBoolean("isOnboardShowed", false)
        if(isOnboardShowed == false) {
            binding.animationView.playAnimation()
            val sharedPrefEditor = activity?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)?.edit()
            sharedPrefEditor?.putBoolean("isOnboardShowed", true)
            sharedPrefEditor?.apply()
        }
    }

    private fun onCategoryClick(model: CategoryModel) {
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString(CATEGORY_TYPE, model.category.value)?.apply()
        (activity as MainActivity).navigateTo(R.id.searchFragment)
    }

    private fun onOfferClick(model: VendorModel?) {
        findNavController().navigate(R.id.offerInfoFragment, bundleOf(
            OFFER_MODEL to model
        ))
    }

    private fun onSlideClick(sliderModel: SliderModel?) {
        val dialog = SliderInfoBottomSheetFragment(sliderModel)
        dialog.show(parentFragmentManager, SliderInfoBottomSheetFragment::class.java.canonicalName)
    }

    override fun setupObservers() {
        viewModel.sliderListResult.observe(
            viewLifecycleOwner,
            ::consumeSliderListResult
        )
        viewModel.filterMaxPriceResult.observe(
            viewLifecycleOwner,
            ::consumeFilterMaxPriceResult
        )
        viewModel.popularListResult.observe(
            viewLifecycleOwner,
            ::consumePopularOfferListResult
        )
    }

    private fun consumeFilterMaxPriceResult(filterMaxPrice: Long?) {
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putLong(FILTER_MAX_PRICE, filterMaxPrice?:20000)?.apply()
    }

    private fun consumePopularOfferListResult(resource: Resource<ArrayList<VendorModel?>>) {
        when(resource) {
            is  Resource.Loading -> {
                binding.offerShimmerView.startShimmer()
            }
            is  Resource.Success -> {
                resource.data?.let { offerAdapter?.submitList(it) }
                binding.offerShimmerView.stopShimmer()
                binding.offerShimmerView.isVisible = false
            }
            is  Resource.Error -> {
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun consumeSliderListResult(resource: Resource<ArrayList<SliderModel?>>) {
        when(resource) {
            is  Resource.Loading -> {
                binding.sliderShimmerView.startShimmer()
            }
            is  Resource.Success -> {
                resource.data?.let { viewPagerAdapter?.submitList(it) }
                binding.sliderShimmerView.stopShimmer()
                binding.sliderShimmerView.isVisible = false
            }
            is  Resource.Error -> {
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


}