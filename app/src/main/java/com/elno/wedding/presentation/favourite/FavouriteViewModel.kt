package com.elno.wedding.presentation.favourite

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
class FavouriteViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
): ViewModel() {

    private val _favouriteListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = SingleLiveData()
    val favouriteListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _favouriteListResult


    fun getFavouriteList() {
        _favouriteListResult.value = Resource.Loading()
        databaseReference.collection("vendors").whereEqualTo("overdue", false).get().addOnSuccessListener { result ->
            val offerList = arrayListOf<VendorModel?>()
            for (documentSnapshot in result) {
                val offerModel = documentSnapshot.toObject(VendorModel::class.java)
                offerList.add(offerModel)
            }
            offerList.sortBy {it?.order}
            _favouriteListResult.value = Resource.Success(offerList)
        }.addOnFailureListener{
            _favouriteListResult.value = Resource.Error("Error while loading")
        }
    }

}