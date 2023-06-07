package com.renata.data.user.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResult(
    val id: String? = null,
    val email: String? = null,
    val token: String? = null
) : Parcelable