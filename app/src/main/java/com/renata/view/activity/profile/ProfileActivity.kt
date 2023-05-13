package com.renata.view.activity.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.renata.R
import com.renata.databinding.ActivityProfileBinding
import com.renata.view.activity.setavatar.AvatarActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        profileBinding.saveButton.setOnClickListener { saveChanges() }
        profileBinding.changeAvatarButton.setOnClickListener { goToChangeAvatar() }
    }

    private fun goToChangeAvatar() {
        val moveToChangeAvatar = Intent(this, AvatarActivity::class.java)
        startActivity(moveToChangeAvatar)
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
    }

    private fun saveChanges() {
        showAlert(
            getString(R.string.save_changes),
            getString(R.string.save_changes_cause)
        )
        { goToAccount() }
    }

    private fun showAlert(
        title: String,
        message: String,
        positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(this).apply {
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

    private fun goToAccount() {

    }
}