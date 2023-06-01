package com.renata.data.user.verifyemail

import com.google.gson.annotations.SerializedName

data class VerifyEmailRequest(
    @SerializedName("id") val id: String,
    @SerializedName("otp") val otp: Int
)