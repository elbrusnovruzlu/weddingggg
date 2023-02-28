package com.elno.wedding.presentation.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.elno.wedding.common.Resource
import com.elno.wedding.common.SingleLiveData
import com.elno.wedding.common.Static
import com.elno.wedding.domain.model.CategoryModel
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

    private val _popularListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = SingleLiveData()
    val popularListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _popularListResult

    private val _categoryListResult: SingleLiveData<Resource<ArrayList<CategoryModel?>>> = SingleLiveData()
    val categoryListResult: SingleLiveData<Resource<ArrayList<CategoryModel?>>> = _categoryListResult

    fun getSliderList() {
        _sliderListResult.value = Resource.Loading()
        databaseReference.collection("slider").get().addOnSuccessListener { result ->
            val sliderList = arrayListOf<SliderModel?>()
            for (documentSnapshot in result) {
                val sliderModel = documentSnapshot.toObject(SliderModel::class.java)
                sliderList.add(sliderModel)
            }
            _sliderListResult.value = Resource.Success(sliderList)
        }.addOnFailureListener{
            _sliderListResult.value = Resource.Error("Error while loading")
        }
    }

    fun getCategoryList() {
        _categoryListResult.value = Resource.Loading()
        databaseReference.collection("categories").get().addOnSuccessListener { result ->
            val categoryList = arrayListOf<CategoryModel?>()
            for (documentSnapshot in result) {
                val categoryModel = documentSnapshot.toObject(CategoryModel::class.java)
                categoryList.add(categoryModel)
            }
            Static.filterModel.categories = categoryList
            getPopularOfferList()
            _categoryListResult.value = Resource.Success(categoryList)
        }.addOnFailureListener{
            _categoryListResult.value = Resource.Error("Error while loading")
        }
    }

    private fun getPopularOfferList() {
        _popularListResult.value = Resource.Loading()
        databaseReference.collection("vendors").whereEqualTo("isPopular", true).whereEqualTo("overdue", false).get().addOnSuccessListener { result ->
            val offerList = arrayListOf<VendorModel?>()
            for (documentSnapshot in result) {
                val offerModel = documentSnapshot.toObject(VendorModel::class.java)
                offerList.add(offerModel)
            }
            offerList.sortBy {it?.order}
            _popularListResult.value = Resource.Success(offerList)
        }.addOnFailureListener{
            _popularListResult.value = Resource.Error("Error while loading")
        }
    }

}