package com.elno.wedding.presentation.onboard

import androidx.navigation.fragment.findNavController
import com.elno.wedding.R
import com.elno.wedding.databinding.FragmentOnboardBinding
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardFragment : BaseFragment<FragmentOnboardBinding>(FragmentOnboardBinding::inflate) {

    override fun setupListeners() {
        binding.continueBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onboardFragment_to_dashboardFragment)
        }
    }
}