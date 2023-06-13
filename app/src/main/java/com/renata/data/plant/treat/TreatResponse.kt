package com.renata.data.plant.treat

import com.google.gson.annotations.SerializedName

data class TreatResponse(
    @field:SerializedName("data")
    val data: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Number,

    @field:SerializedName("success")
    val success: Boolean
)
