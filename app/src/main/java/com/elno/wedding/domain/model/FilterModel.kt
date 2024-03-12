package com.elno.wedding.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterModel(
    val type: String?= null,
    val name: String?= null,
    val title: HashMap<String, String>? = null,
    val choices: ArrayList<HashMap<String, String>>? = null
): Parcelable
