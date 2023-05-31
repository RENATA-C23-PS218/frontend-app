package com.renata.view.activity.authpass

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class AuthPassViewModel(application: Application) : ViewModel() {
    private val repository = RenataRepository(application)
    fun resetAuthentication(email: String, otp: Int) = repository.resetAuthentication(email, otp)
    fun resendOTPReset(email: String) = repository.userForgotPass(email)
}