package com.renata.view.fragment.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.renata.R
import com.renata.databinding.FragmentAccountBinding
import com.renata.view.activity.splash.SplashScreenActivity
import com.renata.view.fragment.profile.ProfileFragment
import com.renata.view.fragment.settings.SettingFragment

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val accountBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return accountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goToSetting()
        changeProfile()
        logout()
    }

    private fun changeProfile() {
        accountBinding.editProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_container, profileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToSetting() {
        accountBinding.settingButton.setOnClickListener {
            val settingFragment = SettingFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_container, settingFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun logout() {
        accountBinding.logoutButton.setOnClickListener {
            val intentToSplash = Intent(requireContext(), SplashScreenActivity::class.java)
            startActivity(intentToSplash)
            activity?.finish()
        }
    }

}