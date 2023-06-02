package com.renata.data.user.updateprofile

import com.google.gson.annotations.SerializedName

data class UpdatePhotoResponse(
    @field:SerializedName("status")
    val status: Number,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val data: Data,
)

data class Data(
    @field:SerializedName("url")
    val url: String
)
