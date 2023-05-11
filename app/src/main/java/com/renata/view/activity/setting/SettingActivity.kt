package com.renata.view.activity.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.databinding.ActivitySettingBinding
import com.renata.helper.ViewModelFactory
import com.renata.utils.AlarmReceiver
import com.renata.view.activity.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var settingBinding: ActivitySettingBinding
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(settingBinding.root)

        backToAccount()
        setLanguage()
        settingBinding.offNotifButton.setOnClickListener(this)
        settingBinding.onNotifButton.setOnClickListener(this)
        alarmReceiver = AlarmReceiver()

        // Start Dark Theme
        val switchTheme = settingBinding.switchTheme
        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(SettingViewModel::class.java)

        settingViewModel.getThemeSetting().observe(this) {isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        } // End Dark Theme

    }

    private fun backToAccount() {
        settingBinding.backButton.setOnClickListener {
            val moveToAccount = Intent(this, MainActivity::class.java)
            startActivity(moveToAccount)
        }
    }

    private fun setLanguage() {
        settingBinding.changeLangButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    override fun onClick(v: View) {
        val repeatMessage = getString(R.string.alarm_message)

        when (v.id) {
            R.id.onNotifButton -> alarmReceiver.setRepeatingAlarm(this, repeatMessage)
            R.id.offNotifButton -> alarmReceiver.cancelAlarm(this)
        }
    }

}