package com.renata.data.user.verifyresetpass

import com.google.gson.annotations.SerializedName

data class VerifyResetPassRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: Int
)