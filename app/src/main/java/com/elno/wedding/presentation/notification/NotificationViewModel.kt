package com.elno.wedding.presentation.notification

import androidx.lifecycle.ViewModel
import com.elno.wedding.common.Resource
import com.elno.wedding.common.SingleLiveData
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.domain.model.SliderModel
import com.elno.wedding.presentation.search.filter.Category
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
) : ViewModel() {


    private val _vendorResult: SingleLiveData<Resource<VendorModel?>> = SingleLiveData()
    val vendorResult: SingleLiveData<Resource<VendorModel?>> = _vendorResult

    fun getVendorList(vendorId: String) {
        _vendorResult.value = Resource.Loading()
        databaseReference.collection("vendors").document(vendorId).get().addOnSuccessListener { result ->
            val vendorModel = result.toObject(VendorModel::class.java)
            _vendorResult.value = Resource.Success(vendorModel)
        }.addOnFailureListener {
            _vendorResult.value = Resource.Error("Error while loading")
        }
    }

}