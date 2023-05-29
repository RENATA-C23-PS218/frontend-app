package com.renata.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.renata.view.activity.authentication.AuthViewModel
import com.renata.view.activity.authpass.AuthPassViewModel
import com.renata.view.activity.forgotpass.ForgotPassViewModel
import com.renata.view.activity.login.LoginViewModel
import com.renata.view.activity.main.NavigationViewModel
import com.renata.view.activity.profile.ProfileViewModel
import com.renata.view.activity.register.RegisterViewModel
import com.renata.view.activity.reset.ResetPassViewModel
import com.renata.view.activity.scan.ScanViewModel
import com.renata.view.activity.setavatar.AvatarViewModel
import com.renata.view.activity.setting.SettingViewModel
import com.renata.view.activity.splash.SplashViewModel
import com.renata.view.fragment.account.AccountViewModel
import com.renata.view.fragment.history.HistoryViewModel

class ViewModelFactory(private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(NavigationViewModel::class.java)) {
            return NavigationViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(ForgotPassViewModel::class.java)) {
            return ForgotPassViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(application) as T
        }
//        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
//            return ProfileViewModel(application) as T
//        }
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            return ScanViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(AuthPassViewModel::class.java)) {
            return AuthPassViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(ResetPassViewModel::class.java)) {
            return ResetPassViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(AvatarViewModel::class.java)) {
            return AvatarViewModel(application) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(
            application: Application
        ): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(application)
            }
    }
}