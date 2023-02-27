package com.elno.wedding.presentation.notification

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elno.wedding.MainActivity
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.common.Resource
import com.elno.wedding.databinding.FragmentNotificationBinding
import com.elno.wedding.domain.model.NotificationModel
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.presentation.adapter.NotificationAdapter
import com.elno.wedding.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::inflate) {

    private val viewModel: NotificationViewModel by viewModels()
    private val adapter = NotificationAdapter {
        showNotification(it)
    }

    override fun setupViews() {
        binding.recyclerView.adapter = adapter
        val sharedPref: SharedPreferences? = context?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
        val time = sharedPref?.getLong("deleteTime", 0) ?: 0
        viewModel.getNotificationList(time)
    }

    override fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.delete.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.sure_to_delete))
                .setPositiveButton(R.string.yes) { _, _ ->
                    val sharedPref: SharedPreferences? = context?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
                    sharedPref?.edit()?.putLong("deleteTime", System.currentTimeMillis())?.apply()
                    binding.emptyLayout.isVisible = true
                    binding.recyclerView.isVisible = false
                    binding.delete.isVisible = false
                    adapter.submitList(mutableListOf())
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        }
    }

    override fun setupObservers() {
        viewModel.vendorResult.observe(
            viewLifecycleOwner,
            ::consumeVendorResult
        )
        viewModel.notificationListResult.observe(
            viewLifecycleOwner,
            ::consumeNotificationListResult
        )
    }

    private fun consumeNotificationListResult(resource: Resource<ArrayList<NotificationModel?>>?) {
        when(resource) {
            is  Resource.Loading -> {
                binding.loading.isVisible = true
            }
            is  Resource.Success -> {
                binding.loading.isVisible = false
                if(resource.data.isNullOrEmpty()) {
                    binding.emptyLayout.isVisible = true
                    binding.recyclerView.isVisible = false
                    binding.delete.isVisible = false
                }
                else {
                    binding.emptyLayout.isVisible = false
                    binding.recyclerView.isVisible = true
                    binding.delete.isVisible = true
                    adapter.submitList(resource.data)
                    checkNotification(resource.data)
                }
            }
            is  Resource.Error -> {
                binding.loading.isVisible = false
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun consumeVendorResult(resource: Resource<VendorModel?>?) {
        when(resource) {
            is  Resource.Loading -> {
                binding.loading.isVisible = true
            }
            is  Resource.Success -> {
                binding.loading.isVisible = false
                resource.data?.let { goToInfoScreen(it) }
            }
            is  Resource.Error -> {
                binding.loading.isVisible = false
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkNotification(notificationList: ArrayList<NotificationModel?>) {
        if (arguments?.containsKey(Constants.NOTIFICATION_ID) == true) {
            val notificationId = arguments?.getString(Constants.NOTIFICATION_ID)
            arguments?.clear()
            notificationList.find { it?.id == notificationId }?.let {
                showNotification(it)
            }
        }
    }

    private fun showNotification(notificationModel: NotificationModel?) {
        val dialog = NotificationInfoBottomSheetFragment(notificationModel) { action, id ->
            if(action == NotificationAction.OPEN_VENDOR.value) {
                id?.let { it -> viewModel.getVendor(it) }
            }
            else if(action == NotificationAction.NEW_CATEGORY.value) {
                id?.let {
                    val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("sharedFile", Context.MODE_PRIVATE)
                    sharedPreferences?.edit()?.putString(Constants.CATEGORY_TYPE, it)?.apply()
                    (activity as MainActivity).navigateTo(R.id.searchFragment)
                }
            }
        }
        dialog.show(
            parentFragmentManager,
            NotificationInfoBottomSheetFragment::class.java.canonicalName
        )
    }

    private fun goToInfoScreen(vendorModel: VendorModel) {
        findNavController().navigate(
            R.id.offerInfoFragment, bundleOf(
                Constants.OFFER_MODEL to vendorModel
            )
        )
    }

}