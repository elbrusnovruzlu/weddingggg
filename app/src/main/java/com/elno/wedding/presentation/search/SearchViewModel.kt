package com.elno.wedding.presentation.search

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
import com.elno.wedding.domain.model.FilteringModel
import com.elno.wedding.domain.model.VendorModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val databaseReference: FirebaseFirestore
) : ViewModel() {

    private val client = ClientSearch(
        applicationID = ApplicationID("0Y1BH35JOE"),
        apiKey = APIKey("3c431da2c6351d732449fe9ff00ebe73")
    )

    var categoryType: String? = null
    var filteringModel: FilteringModel = FilteringModel()

    var lastIndex: Int = 0
    var searchQuery = ""

    private val _vendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = SingleLiveData()
    val vendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _vendorListResult

    private val _moreVendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = SingleLiveData()
    val moreVendorListResult: SingleLiveData<Resource<ArrayList<VendorModel?>>> = _moreVendorListResult

    var filterResult: ArrayList<FilterModel>? = null

    fun getFilter() {
        if (categoryType != null) {
            databaseReference.collection("categories").document(categoryType.toString()).collection("filter").orderBy("order").get().addOnSuccessListener { result ->
                val filterList = arrayListOf<FilterModel>()
                for (documentSnapshot in result) {
                    val filterModel = documentSnapshot.toObject(FilterModel::class.java)
                    filterList.add(filterModel)
                }
                filterResult = filterList
            }
        }
    }

    fun getVendorList() {
        _vendorListResult.value = Resource.Loading()
        val collectionReference =
            if (categoryType == null) databaseReference.collection("vendors")
            else databaseReference.collection("vendors").whereEqualTo("type", categoryType)

        val minReference = if(filteringModel.minPrice == 0L) collectionReference else collectionReference.whereGreaterThanOrEqualTo("minPrice", filteringModel.minPrice)
        var requestReference = if(filteringModel.maxPrice == 0L) minReference else minReference.whereLessThanOrEqualTo("minPrice", filteringModel.maxPrice)

        filteringModel.filterMap?.forEach {
            requestReference = requestReference.whereIn(FieldPath.of("filter", it.key), it.value)
        }


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


    fun searchVendorList(isMore: Boolean = false) {
        if(!isMore) _vendorListResult.value = Resource.Loading()
        viewModelScope.launch {
            val index = client.initIndex(indexName = IndexName("vendors"))
            val response = index.run {
                val query = query {
                    filters {
                        and {
                            if(categoryType != null) facet("type", categoryType.toString())
                            filteringModel.filterMap?.forEach { filter ->
                                filter.value.forEach {
                                    facet("filter.${filter.key}", it)
                                }
                            }
                            if(filteringModel.minPrice != 0L) {
                                comparison("minPrice", NumericOperator.GreaterOrEquals, filteringModel.minPrice)
                            }
                            if(filteringModel.maxPrice != 0L) {
                                comparison("minPrice", NumericOperator.LessOrEquals, filteringModel.maxPrice)
                            }
                            comparison("order", NumericOperator.Greater, lastIndex)
                        }
                    }
                }
                if(searchQuery.isNotEmpty()) query.query = searchQuery

                search(query)
            }
            val results: List<VendorModel> = response.hits.deserialize(VendorModel.serializer())
            lastIndex = if(results.isEmpty()) Int.MAX_VALUE else results.last().order?: Int.MAX_VALUE
            Log.d("TAHIRAA", results.map { it.id }.toString())
            if(!isMore) _vendorListResult.value = Resource.Success(ArrayList(results))
            else _moreVendorListResult.value = Resource.Success(ArrayList(results))
        }
    }

}