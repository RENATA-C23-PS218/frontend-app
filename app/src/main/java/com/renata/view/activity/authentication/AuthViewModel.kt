package com.renata.view.activity.authentication

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class AuthViewModel(application: Application) : ViewModel() {
    private val repository = RenataRepository(application)
    fun userAuthentication(id: String, otp: Int) = repository.authentication(id, otp)
    fun resendOTP(id: String) = repository.resendOTP(id)
}