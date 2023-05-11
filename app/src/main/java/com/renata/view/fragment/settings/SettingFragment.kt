package com.renata.view.fragment.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.databinding.FragmentSettingBinding
import com.renata.utils.AlarmReceiver
import com.renata.utils.ViewModelFactory
import com.renata.view.fragment.account.AccountFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

        // Start Dark Theme
        val switchTheme = settingBinding.switchTheme
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(SettingViewModel::class.java)

        settingViewModel.getThemeSetting()
            .observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
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