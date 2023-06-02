package com.renata.view.activity.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.renata.R
import com.renata.data.retrofit.ApiConfig
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResult
import com.renata.data.user.updateprofile.UpdatePhotoResponse
import com.renata.data.user.updateprofile.UpdateProfileResponse
import com.renata.databinding.ActivityProfileBinding
import com.renata.view.activity.main.NavigationActivity
import com.renata.view.activity.setavatar.AvatarActivity
import com.renata.view.activity.setavatar.AvatarViewModel
import com.renata.view.fragment.account.AccountFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    var avatarViewModel: AvatarViewModel = AvatarViewModel()
    private lateinit var loginPreference: LoginPreferences
    private lateinit var loginResult: LoginResult

    companion object {
        private const val TAG = "Profile Activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        loginPreference = LoginPreferences(this)
        loginResult = loginPreference.getUser()
        val sendToken = loginResult.token

        profileViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(ProfileViewModel::class.java)

        avatarViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(AvatarViewModel::class.java)

        val intent = intent.getStringExtra("token")
        val token = "Bearer $intent"
        getDataProfile(token)
        getPhoto()

        profileBinding.saveButton.setOnClickListener {
            loginPreference = LoginPreferences(this)
            loginResult = loginPreference.getUser()
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

    private fun getPhoto(){
//        val image = intent.getStringExtra("image")
//            Glide.with(this@ProfileActivity)
//                .load(image)
//                .centerCrop()
//                .into(profileBinding.profileImage)
        avatarViewModel.getPhoto().observe(this){
            val data = it.data
            Glide.with(this@ProfileActivity)
                .load(data.url)
                .centerCrop()
                .into(profileBinding.profileImage)
        }

    }

    private fun saveChanges(firstName: String, lastName: String, phone: String, address: String) {
        val intent = intent.getStringExtra("token")
        val token = "Bearer $intent"
        if (token != null) {
            profileViewModel.setUserProfile(token, firstName, lastName, phone, address)
        }
        profileViewModel.getUser().observe(this) {
            if (it.success) {
                showAlert(
                    getString(R.string.save_changes),
                    getString(R.string.save_changes_cause)
                )
                {
                    getDataProfile(token)// tes
                    onBackPressed()
                }
            } else {
                Toast.makeText(this@ProfileActivity, "Change Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getDataProfile(token: String){
        if (token !=null){
            profileViewModel.userProfile(token)
            profileViewModel.getUserProfile().observe(this){
                if (it !=null) {
                    profileBinding.apply {
                        val data = it.data
                        edFirstName.setText(data.first_name)
                        edLastName.setText(data.last_name)
                        edPhone.setText(data.phone)
                        edAddress.setText(data.address)
                        // testing. masih salah
//                        val intent = Intent(this@ProfileActivity, AccountFragment::class.java)
//                        intent.putExtra("username", data.full_name)
//                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun goToChangeAvatar() {
        val token = intent.getStringExtra("token")
        val moveToChangeAvatar = Intent(this, AvatarActivity::class.java)
        moveToChangeAvatar.putExtra("token", token)
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

    // without ViewModel
    private fun saveChangess(firstName: String, lastName: String, phone: String, address: String) {

        val intent = intent.getStringExtra("token")
        val token = "Bearer $intent"
        val client =
            ApiConfig.getApiService().updateProfile(token, firstName, lastName, phone, address)
        client.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                val responseBody = response.body()
                Log.d(TAG, "onResponse: $responseBody")
                if (responseBody != null) {
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