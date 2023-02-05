package com.elno.wedding.presentation.search

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
class SearchViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
) : ViewModel() {

    var categoryType = Category.ALL.value
    var filterMaxPrice = 20000f
    var minPrice = 0f
    var maxPrice = 20000f

    private val _vendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> =
        SingleLiveData()
    val vendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _vendorListResult

    fun getVendorList() {
        _vendorListResult.value = Resource.Loading()
        val collectionReference =
            if (categoryType == Category.ALL.value) databaseReference.collection("vendors")
            else databaseReference.collection("vendors").whereEqualTo("type", categoryType)

        collectionReference.whereGreaterThanOrEqualTo("minPrice", minPrice).whereLessThanOrEqualTo("minPrice", maxPrice).get().addOnSuccessListener { result ->
            val vendorList = arrayListOf<VendorModel?>()
            for (documentSnapshot in result) {
                val vendorModel = documentSnapshot.toObject(VendorModel::class.java)
                vendorList.add(vendorModel)
            }
            _vendorListResult.value = Resource.Success(vendorList)
        }.addOnFailureListener {
            _vendorListResult.value = Resource.Error("Error while loading")
        }
    }

}