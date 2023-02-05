package com.elno.wedding.domain.model

import android.os.Parcelable
import com.elno.wedding.presentation.search.filter.Category
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryModel(
    val titleResId: Int,
    val imageResId: Int,
    val category: Category
): Parcelable