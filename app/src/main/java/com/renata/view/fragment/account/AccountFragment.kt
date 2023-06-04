package com.renata.view.fragment.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.databinding.FragmentAccountBinding
import com.renata.view.activity.profile.ProfileActivity
import com.renata.view.activity.profile.ProfileViewModel
import com.renata.view.activity.setting.SettingActivity
import com.renata.view.activity.splash.SplashScreenActivity

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val accountBinding get() = _binding!!
    private lateinit var loginPreference: LoginPreferences
    var accountViewModel: ProfileViewModel = ProfileViewModel()
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
        accountViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)
        val token = loginResult.token
        getData(token)
        goToSetting()
        changeProfile(token)
        logout()
    }

    private fun changeProfile(token: String?) {
        accountBinding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }

    private fun getData(token: String?) {
        val getToken = "Bearer $token"
        if (getToken != null) {
            accountViewModel.userProfile(getToken)
            accountViewModel.getUserProfile().observe(viewLifecycleOwner) {
                val data = it.data
                accountBinding.tvProfileName.text = data.full_name
                accountBinding.tvProfileEmail.text = data.email
            }
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