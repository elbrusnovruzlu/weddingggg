package com.elno.wedding.domain.model

data class FilterModel(
    var maxPrice: Long = 20000,
    var categories: ArrayList<CategoryModel?>? = null
)