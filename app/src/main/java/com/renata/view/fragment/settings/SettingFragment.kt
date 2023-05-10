package com.renata.view.fragment.settings

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
import com.renata.view.fragment.account.AccountFragment

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
        backToAccount()
        setLanguage()
        settingBinding.offNotifButton.setOnClickListener(this)
        settingBinding.onNotifButton.setOnClickListener(this)
        alarmReceiver = AlarmReceiver()
    }

    private fun setLanguage() {
        settingBinding.changeLangButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    override fun onClick(v: View) {
        val repeatMessage = getString(R.string.alarm_message)

        when (v.id) {
            R.id.onNotifButton -> alarmReceiver.setRepeatingAlarm(requireContext(), repeatMessage)
            R.id.offNotifButton -> alarmReceiver.cancelAlarm(requireContext())
        }
    }

    private fun backToAccount() {
        settingBinding.backButton.setOnClickListener {
            val accountFragment = AccountFragment()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.layout_container, accountFragment)
                .addToBackStack(null)
                .commit()
        }
    }

}