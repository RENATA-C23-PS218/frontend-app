package com.renata.view.activity.login

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class LoginViewModel(application: Application) : ViewModel() {
    private val repository = RenataRepository(application)
    fun userLogin(email: String, password: String) = repository.login(email, password)
}