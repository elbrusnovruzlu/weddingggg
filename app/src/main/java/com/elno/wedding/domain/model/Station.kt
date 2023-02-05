package com.elno.wedding.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Station(
    val name: String,
    val freeRacks: String,
    val bikes: String,
    val lan: Double,
    val long: Double
): Parcelable
