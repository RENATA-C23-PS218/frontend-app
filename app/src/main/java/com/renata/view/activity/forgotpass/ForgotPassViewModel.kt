package com.renata.view.activity.forgotpass

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class ForgotPassViewModel(application: Application) : ViewModel() {
    private val repository = RenataRepository(application)
    fun userForgotPass(email: String) = repository.userForgotPass(email)
}