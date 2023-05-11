package com.renata.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.renata.view.fragment.settings.SettingPreferences
import com.renata.view.fragment.settings.SettingViewModel

class ViewModelFactory(private val pref: SettingPreferences) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(pref) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}