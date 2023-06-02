package com.renata.data.user.updateprofile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val data: DataProfile
)

data class DataProfile(
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("full_name")
    val full_name: String,

    @field:SerializedName("first_name")
    val first_name: String,

    @field:SerializedName("last_name")
    val last_name: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("address")
    val address: String
)


