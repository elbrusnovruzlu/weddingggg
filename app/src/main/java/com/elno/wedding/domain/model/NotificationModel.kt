package com.elno.wedding.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationModel(
    val action: String?,
    val vendorId: String?,
    val title: String?,
    val description: String?,
    val imageUrl: String?
): Parcelable
