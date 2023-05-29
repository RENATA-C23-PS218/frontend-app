package com.renata.view.activity.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.data.retrofit.ApiConfig
import com.renata.data.user.updateprofile.UpdateProfileResponse
import com.renata.databinding.ActivityProfileBinding
import com.renata.view.activity.main.NavigationActivity
import com.renata.view.activity.setavatar.AvatarActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        profileViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)

        profileBinding.saveButton.setOnClickListener {
            val firstName = profileBinding.edFirstName.text.toString()
            val lastName = profileBinding.edLastName.text.toString()
            val phone = profileBinding.edPhone.text.toString()
            val address = profileBinding.edAddress.text.toString()
            saveChanges(firstName, lastName, phone, address)
        }

        profileBinding.changeAvatarButton.setOnClickListener { goToChangeAvatar() }
        profileBinding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveChanges(firstName: String, lastName: String, phone: String, address: String) {
        if (firstName != null) {
            profileViewModel.setUserProfile(firstName, lastName, phone, address)
        }
        profileViewModel.getUser().observe(this) {
            if (it.status) {
                showAlert(
                    getString(R.string.save_changes),
                    getString(R.string.save_changes_cause)
                )
                { onBackPressed() }
            } else {
                Toast.makeText(this@ProfileActivity, "Change Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToChangeAvatar() {
        val moveToChangeAvatar = Intent(this, AvatarActivity::class.java)
        startActivity(moveToChangeAvatar)
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
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

    companion object {
        private const val TAG = "Profile Activity"
    }

    // without ViewModel
    private fun saveChangess(firstName: String, lastName: String, phone: String, address: String) {

        val client = ApiConfig.getApiService().updateProfile(firstName, lastName, phone, address)
        client.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if (responseBody != null && !responseBody.status) {
                    Toast.makeText(this@ProfileActivity, "change success", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@ProfileActivity, NavigationActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d(TAG, "onFailure: ${response.message()}")
                    Toast.makeText(this@ProfileActivity, "Change Failed", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Log.d(TAG, "onfailure: ${t.message}")
            }

        })
    }

}