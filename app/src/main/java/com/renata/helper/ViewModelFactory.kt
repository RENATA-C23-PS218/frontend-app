package com.renata.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.renata.view.activity.setting.SettingPreferences
import com.renata.view.activity.setting.SettingViewModel

class ViewModelFactory(private val pref: SettingPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(pref) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}