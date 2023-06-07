package com.renata.data.user.register

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val data: Data,
) : Parcelable

@Parcelize
data class Data(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("email")
    val email: String
) : Parcelable
