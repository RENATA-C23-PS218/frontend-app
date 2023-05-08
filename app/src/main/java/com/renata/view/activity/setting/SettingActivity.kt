package com.renata.view.activity.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.renata.R
import com.renata.databinding.ActivitySettingBinding
import com.renata.utils.AlarmReceiver
import com.renata.view.activity.main.MainActivity

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