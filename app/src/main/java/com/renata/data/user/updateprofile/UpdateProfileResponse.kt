package com.renata.data.user.updateprofile

import com.google.gson.annotations.SerializedName
import com.renata.data.user.login.Data

data class UpdateProfileResponse(
    @field:SerializedName("status")
    val status: Number,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val data: DataProfile,
)