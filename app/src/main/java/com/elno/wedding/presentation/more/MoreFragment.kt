package com.elno.wedding.presentation.more

import androidx.navigation.fragment.findNavController
import com.elno.wedding.R
import com.elno.wedding.databinding.FragmentFavouriteBinding
import com.elno.wedding.databinding.FragmentMoreBinding
import com.elno.wedding.databinding.FragmentOfferInfoBinding
import com.elno.wedding.databinding.FragmentSearchBinding
import com.elno.wedding.presentation.base.BaseFragment
import com.elno.wedding.presentation.more.MoreFragment.Constants.PRIVACY_POLICY
import com.elno.wedding.presentation.more.MoreFragment.Constants.TERMS_AND_CONDITIONS
import com.elno.wedding.presentation.search.filter.FilterBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : BaseFragment<FragmentMoreBinding>(FragmentMoreBinding::inflate) {

    override fun setupViews() {
        binding.contactUs.icon.setImageResource(R.drawable.ic_contact_us)
        binding.privacyPolicy.icon.setImageResource(R.drawable.ic_privacy)
        binding.termsAndCondition.icon.setImageResource(R.drawable.ic_terms)

        binding.contactUs.title.text = getString(R.string.contact_us)
        binding.privacyPolicy.title.text = getString(R.string.privacy_policy)
        binding.termsAndCondition.title.text = getString(R.string.terms_and_condition)
    }

    override fun setupListeners() {
        binding.contactUs.root.setOnClickListener {
            findNavController().navigate(R.id.contactUsFragment)
        }
        binding.privacyPolicy.root.setOnClickListener {
            val dialog = PrivacyPolicyBottomSheetFragment(
                getString(R.string.privacy_policy),
                PRIVACY_POLICY
            )
            dialog.show(parentFragmentManager, PrivacyPolicyBottomSheetFragment::class.java.canonicalName)
        }
        binding.termsAndCondition.root.setOnClickListener {
            val dialog = PrivacyPolicyBottomSheetFragment(
                getString(R.string.terms_and_condition),
                TERMS_AND_CONDITIONS
            )
            dialog.show(parentFragmentManager, PrivacyPolicyBottomSheetFragment::class.java.canonicalName)
        }
    }

    object Constants {
        const val PRIVACY_POLICY = "In malesuada justo tellus integer dolor eget vel duis magna. Proin lectus tincidunt aenean interdum pellentesque tempor. Cursus nunc mattis libero ut congue et sem. Morbi donec risus eget nibh ultrices diam et. Quisque venenatis nunc in turpis morbi pellentesque facilisis. Fringilla suspendisse in mattis semper malesuada quis aliquam condimentum rutrum.\n" +
                "Suspendisse nisi at a eget rhoncus eu. Tellus neque sed turpis etiam. Aliquam massa tincidunt pretium fringilla vestibulum pretium cras. Vitae netus euismod facilisi quam elementum non risus faucibus. Elit imperdiet diam eget venenatis elementum. Proin facilisis sed vitae amet ullamcorper volutpat tortor ultrices porttitor. Lectus iaculis sed sit vitae enim fringilla lectus turpis aenean. Proin risus vestibulum a sed bibendum sit mi. Convallis iaculis purus erat lacus sem ac leo. Sed mattis gravida morbi erat felis pellentesque sodales libero."
        const val TERMS_AND_CONDITIONS = "In malesuada justo tellus integer dolor eget vel duis magna. Proin lectus tincidunt aenean interdum pellentesque tempor. Cursus nunc mattis libero ut congue et sem. Morbi donec risus eget nibh ultrices diam et. Quisque venenatis nunc in turpis morbi pellentesque facilisis. Fringilla suspendisse in mattis semper malesuada quis aliquam condimentum rutrum.\n" +
                "Suspendisse nisi at a eget rhoncus eu. Tellus neque sed turpis etiam. Aliquam massa tincidunt pretium fringilla vestibulum pretium cras. Vitae netus euismod facilisi quam elementum non risus faucibus. Elit imperdiet diam eget venenatis elementum. Proin facilisis sed vitae amet ullamcorper volutpat tortor ultrices porttitor. Lectus iaculis sed sit vitae enim fringilla lectus turpis aenean. Proin risus vestibulum a sed bibendum sit mi. Convallis iaculis purus erat lacus sem ac leo. Sed mattis gravida morbi erat felis pellentesque sodales libero."

    }

}