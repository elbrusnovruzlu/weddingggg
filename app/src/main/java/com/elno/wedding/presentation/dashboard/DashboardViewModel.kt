package com.elno.wedding.presentation.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.elno.wedding.common.Resource
import com.elno.wedding.common.SingleLiveData
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.domain.model.SliderModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
): ViewModel() {

    private val _sliderListResult: SingleLiveData<Resource<ArrayList<SliderModel?>>> = SingleLiveData()
    val sliderListResult: SingleLiveData<Resource<ArrayList<SliderModel?>>> = _sliderListResult

    private val _filterMaxPriceResult: SingleLiveData<Long> = SingleLiveData()
    val filterMaxPriceResult: SingleLiveData<Long> = _filterMaxPriceResult

    private val _popularListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = SingleLiveData()
    val popularListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _popularListResult

    fun getSliderList() {
        _sliderListResult.value = Resource.Loading()
        databaseReference.collection("slider").get().addOnSuccessListener { result ->
            val sliderList = arrayListOf<SliderModel?>()
            var filterMaxPrice = 0L
            for ((index, documentSnapshot) in result.withIndex()) {
                if(index == result.size()-1) {
                    filterMaxPrice = documentSnapshot.data["filterMaxPrice"] as Long
                }
                else {
                    val sliderModel = documentSnapshot.toObject(SliderModel::class.java)
                    sliderList.add(sliderModel)
                }
            }
            _filterMaxPriceResult.value = filterMaxPrice
            _sliderListResult.value = Resource.Success(sliderList)
        }.addOnFailureListener{
            _sliderListResult.value = Resource.Error("Error while loading")
        }
    }

    fun getPopularOfferList() {
        _popularListResult.value = Resource.Loading()
        databaseReference.collection("vendors").whereEqualTo("isPopular", true).get().addOnSuccessListener { result ->
            val offerList = arrayListOf<VendorModel?>()
            for (documentSnapshot in result) {
                val offerModel = documentSnapshot.toObject(VendorModel::class.java)
                offerList.add(offerModel)
            }
            _popularListResult.value = Resource.Success(offerList)
        }.addOnFailureListener{
            _popularListResult.value = Resource.Error("Error while loading")
        }
    }

}