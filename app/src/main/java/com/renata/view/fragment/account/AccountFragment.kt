package com.renata.view.fragment.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.renata.databinding.FragmentAccountBinding
import com.renata.view.activity.login.LoginActivity
import com.renata.view.activity.setting.SettingActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val accountBinding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return accountBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return
        }

        accountBinding.settingButton.setOnClickListener { goToSetting() }
        accountBinding.logoutButton.setOnClickListener { signOut() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToSetting() {
        val intentToSetting = Intent(requireContext(), SettingActivity::class.java)
        startActivity(intentToSetting)
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

}