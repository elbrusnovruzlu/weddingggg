package com.elno.wedding.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.filters
import com.algolia.search.dsl.query
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.filter.NumericOperator
import com.elno.wedding.common.Resource
import com.elno.wedding.common.SingleLiveData
import com.elno.wedding.common.Static
import com.elno.wedding.domain.model.CategoryModel
import com.elno.wedding.domain.model.FilterModel
import com.elno.wedding.domain.model.VendorModel
import com.elno.wedding.domain.model.SliderModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
): ViewModel() {

    private val client = ClientSearch(
        applicationID = ApplicationID("0Y1BH35JOE"),
        apiKey = APIKey("3c431da2c6351d732449fe9ff00ebe73")
    )

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
            Static.categories = categoryList
            getPopularOfferList()
            _categoryListResult.value = Resource.Success(categoryList)
        }.addOnFailureListener{
            _categoryListResult.value = Resource.Error("Error while loading")
        }
    }

    private fun getPopularOfferList() {
        _popularListResult.value = Resource.Loading()
        viewModelScope.launch {
            val index = client.initIndex(indexName = IndexName("vendors"))
            val response = index.run {
                val query = query {
                    filters {
                        and {
                            facet("isPopular", true)
                            facet("overdue", false)
                        }
                    }
                }
                search(query)
            }
            val results: List<VendorModel> = response.hits.deserialize(VendorModel.serializer())
            _popularListResult.value = Resource.Success(ArrayList(results))
        }
    }

}