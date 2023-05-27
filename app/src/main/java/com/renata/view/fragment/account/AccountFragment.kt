package com.renata.view.fragment.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.renata.R
import com.renata.databinding.FragmentAccountBinding
import com.renata.view.activity.profile.ProfileActivity
import com.renata.view.activity.setting.SettingActivity
import com.renata.view.activity.splash.SplashScreenActivity

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
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToSetting() {
        accountBinding.settingButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            .replace(R.id.nav_host_fragment_activity_navigation, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun logout() {
        accountBinding.logoutButton.setOnClickListener {
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