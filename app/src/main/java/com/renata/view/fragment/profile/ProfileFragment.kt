package com.renata.view.fragment.profile

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.renata.R
import com.renata.databinding.FragmentProfileBinding
import com.renata.view.fragment.account.AccountFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val profileBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToAccount()
        saveChanges()
    }

    private fun saveChanges() {
        profileBinding.saveButton.setOnClickListener {
            showAlert(
                "Save Changes",
                "The changes you made have been saved"
            )
            { goToAccount() }
        }
    }

    private fun goToAccount() {
        val accountFragment = AccountFragment()
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            .replace(R.id.layout_container, accountFragment)
            .addToBackStack(null)
            .commit()
    }


    private fun showAlert(
        title: String,
        message: String,
        positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                positiveAction.invoke(dialog)
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun backToAccount() {
        profileBinding.backButton.setOnClickListener {
            goToAccount()
        }
    }

}