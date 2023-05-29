package com.renata.data.user.updateprofile

import com.google.gson.annotations.SerializedName


data class UpdateProfileResponse(
    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String
)
