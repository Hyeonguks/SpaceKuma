package com.example.spacekuma.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Feed_Media_Uri_Model(
    val View_Type: Int?,
    val FileName: String?,
    val Feed_Media_Uri: String?
) : Parcelable