package com.elno.wedding.presentation.search

import androidx.lifecycle.ViewModel
import com.elno.wedding.common.Resource
import com.elno.wedding.common.SingleLiveData
import com.elno.wedding.common.Static
import com.elno.wedding.domain.model.VendorModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
) : ViewModel() {

    var categoryType = "all"
    var minPrice: Long = 0
    var maxPrice: Long = 20000

    private val _vendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> =
        SingleLiveData()
    val vendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _vendorListResult

    private val _filterMaxPriceResult: SingleLiveData<Long> = SingleLiveData()
    val filterMaxPriceResult: SingleLiveData<Long> = _filterMaxPriceResult

    fun getFilterMaxPrice() {
        databaseReference.collection("statics").document("filter").get().addOnSuccessListener { result ->
            val filterMaxPrice = result?.data?.get("maxPrice") as? Long
            Static.filterModel.maxPrice = filterMaxPrice  ?: 20000
            maxPrice = filterMaxPrice ?: 20000
            _filterMaxPriceResult.value = filterMaxPrice ?: 20000
        }
    }

    fun getVendorList() {
        _vendorListResult.value = Resource.Loading()
        val collectionReference =
            if (categoryType == "all") databaseReference.collection("vendors")
            else databaseReference.collection("vendors").whereEqualTo("type", categoryType)

        val requestReference = if(minPrice == 0L && maxPrice == Static.filterModel.maxPrice) collectionReference else collectionReference.whereGreaterThanOrEqualTo("minPrice", minPrice).whereLessThanOrEqualTo("minPrice", maxPrice)

        requestReference.whereEqualTo("overdue", false).get().addOnSuccessListener { result ->
            val vendorList = arrayListOf<VendorModel?>()
            for (documentSnapshot in result) {
                val vendorModel = documentSnapshot.toObject(VendorModel::class.java)
                vendorList.add(vendorModel)
            }
            vendorList.sortBy {it?.order}
            _vendorListResult.value = Resource.Success(vendorList)
        }.addOnFailureListener {
            _vendorListResult.value = Resource.Error("Error while loading")
        }
    }

}