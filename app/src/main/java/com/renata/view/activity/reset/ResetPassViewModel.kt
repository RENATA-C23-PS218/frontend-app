package com.renata.view.activity.reset

import android.app.Application
import androidx.lifecycle.ViewModel
import com.renata.data.RenataRepository

class ResetPassViewModel(application: Application) : ViewModel(){
    private val repository = RenataRepository(application)

    fun userResetPass(email: String, password: String, confirmPassword: String) =
        repository.userResetPass(email, password, confirmPassword)
}