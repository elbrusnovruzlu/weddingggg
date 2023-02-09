package com.elno.wedding.presentation.notification

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.elno.wedding.R
import com.elno.wedding.common.Constants
import com.elno.wedding.common.Constants.NOTIFICATION_LIST
import com.elno.wedding.common.Resource
import com.elno.wedding.data.local.LocalDataStore
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
        context?.let {
            val list = LocalDataStore(it).getList<NotificationModel>(NOTIFICATION_LIST)
            list.sortByDescending { notification -> notification?.timestamp }
            binding.delete.isVisible = list.isEmpty().not()
            adapter.submitList(list)
            binding.recyclerView.adapter = adapter
        }

        checkNotification()
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
                    LocalDataStore(it).removeList(NOTIFICATION_LIST)
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

    private fun checkNotification() {
        if (arguments?.containsKey(Constants.NOTIFICATION_MODEL) == true) {
            val notificationModel = arguments?.getParcelable<NotificationModel>(Constants.NOTIFICATION_MODEL)
            arguments?.clear()
            showNotification(notificationModel)
        }
    }

    private fun showNotification(notificationModel: NotificationModel?) {
        val dialog = NotificationInfoBottomSheetFragment(notificationModel) { vendorId ->
            vendorId?.let { it -> viewModel.getVendorList(it) }
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