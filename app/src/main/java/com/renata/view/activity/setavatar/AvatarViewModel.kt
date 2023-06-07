package com.renata.view.activity.setavatar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.renata.data.retrofit.ApiConfig
import com.renata.data.user.updateprofile.UpdatePhotoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AvatarViewModel : ViewModel() {
    val photo = MutableLiveData<UpdatePhotoResponse>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadPhoto(token: String, image: MultipartBody.Part) {
        _isLoading.postValue(true)
        val client = ApiConfig.getApiService().uploadProfilePict(token, image)
        client.enqueue(object : Callback<UpdatePhotoResponse> {
            override fun onResponse(
                call: Call<UpdatePhotoResponse>,
                response: Response<UpdatePhotoResponse>
            ) {
                if (response.isSuccessful) {
                    photo.postValue(response.body())
                }
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<UpdatePhotoResponse>, t: Throwable) {
                t.message?.let { Log.d("Failure", it) }
                _isLoading.postValue(false)
            }

        })
    }

    fun getPhoto(): LiveData<UpdatePhotoResponse> {
        return photo
    }
}