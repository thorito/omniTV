package com.sivsa.omnitv.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        var name: String = "",
        var login: String = "",
        var password: String? = null,
        var phone: String? = null,
        var emergencyContact: Boolean? = false,
        var thumbPhoto: String? = null,
        var thumbPhotoMD5: String? = null,
        var idFB: String? = null,
        var idTenant: String? = null,
        var legalVersion: Int? = null,
        // @Exclude
        var provisioning: Boolean = false
) : Parcelable