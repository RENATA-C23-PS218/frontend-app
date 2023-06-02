package com.renata.view.activity.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.renata.data.retrofit.ApiConfig
import com.renata.data.user.updateprofile.ProfileResponse
import com.renata.data.user.updateprofile.UpdateProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {
    val user = MutableLiveData<UpdateProfileResponse>()
    val userProfile = MutableLiveData<ProfileResponse>()
    fun setUserProfile(token: String, first_name: String, last_name: String, phone: String, address: String) {
        val client = ApiConfig.getApiService().updateProfile(token, first_name, last_name, phone, address)
        client.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                t.message?.let { Log.d("Failure", it) }
            }

        })
    }

    fun userProfile(token : String){
        val client = ApiConfig.getApiService().getProfile(token)
        client.enqueue(object: Callback<ProfileResponse>{
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful){
                    userProfile.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                t.message?.let { Log.d("Failure", it) }
            }

        })
    }

    fun getUser(): LiveData<UpdateProfileResponse> {
        return user
    }

    fun getUserProfile(): LiveData<ProfileResponse> {
        return userProfile
    }
}