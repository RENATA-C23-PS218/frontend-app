package com.renata.view.activity.register

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class RegisterViewModel(application: Application) : ViewModel() {
    private val repository = RenataRepository(application)

    fun userRegister(email: String, password: String, confirmPassword: String) =
        repository.register(email, password, confirmPassword)
}