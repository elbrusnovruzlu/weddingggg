package com.elno.wedding.domain.model

data class FilteringModel(
    var minPrice: Long = 0,
    var maxPrice: Long = 0,
    var filterMap: HashMap<String, ArrayList<String>>?= null
)
