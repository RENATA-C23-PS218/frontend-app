package com.renata.view.fragment.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.renata.R
import com.renata.databinding.FragmentSettingBinding
import com.renata.utils.AlarmReceiver

class SettingFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSettingBinding? = null
    private val settingBinding get() = _binding!!
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return settingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retainInstance = true
        backToAccount()
        alarmReceiver = AlarmReceiver()
        settingBinding.backButton.setOnClickListener { backToAccount() }
        settingBinding.onNotifButton.setOnClickListener(this)
        settingBinding.offNotifButton.setOnClickListener(this)
        settingBinding.changeLangButton.setOnClickListener { setLanguage() }
    }

    private fun backToAccount() {

    }

    private fun setLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.onNotifButton -> alarmReceiver.setRepeatingAlarm(requireContext())
            R.id.offNotifButton -> alarmReceiver.cancelAlarm(requireContext())
        }
    }

}