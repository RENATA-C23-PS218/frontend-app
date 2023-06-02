package com.renata.view.fragment.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.databinding.FragmentAccountBinding
import com.renata.view.activity.profile.ProfileActivity
import com.renata.view.activity.setting.SettingActivity
import com.renata.view.activity.splash.SplashScreenActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val accountBinding get() = _binding!!
    private lateinit var loginPreference: LoginPreferences
    private lateinit var loginResult: LoginResult

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return accountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPreference = LoginPreferences(requireContext())
        loginResult = loginPreference.getUser()
        val token = loginResult.token
        accountBinding.tvProfileEmail.text = loginResult.email
        goToSetting()
        changeProfile(token)
        logout()
    }

    private fun changeProfile(token: String?) {
        accountBinding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("token",token)
            startActivity(intent)
        }
    }

    private fun goToSetting() {
        accountBinding.settingButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout() {
        accountBinding.logoutButton.setOnClickListener {
            loginPreference.removeUser()
            val intentToSplash = Intent(requireContext(), SplashScreenActivity::class.java)
            startActivity(intentToSplash)
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}