package com.elno.wedding.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorModel(
    val id: String? = null,
    val title: String? = null,
    val imageUrl: String? = null,
    val minPrice: Long? = null,
    val type: String? = null,
    val location: Map<String, Double>? = null,
    val description: String? = null,
    val mobile: String? = null,
    val whatsapp: String? = null
): Parcelable