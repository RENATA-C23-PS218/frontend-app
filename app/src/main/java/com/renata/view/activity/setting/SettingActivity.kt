package com.renata.view.activity.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.renata.R
import com.renata.databinding.ActivitySettingBinding
import com.renata.utils.AlarmReceiver

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var settingBinding: ActivitySettingBinding
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(settingBinding.root)

        alarmReceiver = AlarmReceiver()
        settingBinding.onNotifButton.setOnClickListener(this)
        settingBinding.offNotifButton.setOnClickListener(this)
        settingBinding.changeLangButton.setOnClickListener { setLanguage() }
        settingBinding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun setLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.onNotifButton -> alarmReceiver.setRepeatingAlarm(this)
            R.id.offNotifButton -> alarmReceiver.cancelAlarm(this)
        }
    }
}