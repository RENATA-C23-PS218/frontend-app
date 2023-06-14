package com.renata.view.activity.grow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.renata.data.plant.treat.TreatResponse
import com.renata.data.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GrowViewModel : ViewModel() {

    val treat = MutableLiveData<TreatResponse>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setTreat(plant: String, soil: String) {
        val jsonBody = JSONObject()
        jsonBody.put("plant", plant)
        jsonBody.put("soil", soil)
        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        _isLoading.postValue(true)
        val client = ApiConfig.getApiService().treatPlant(requestBody)
        client.enqueue(object : Callback<TreatResponse> {
            override fun onResponse(call: Call<TreatResponse>, response: Response<TreatResponse>) {
                if (response.isSuccessful) {
                    treat.postValue(response.body())
                }
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<TreatResponse>, t: Throwable) {
                t.message?.let { Log.d("Failure", it) }
                _isLoading.postValue(false)
            }

        })
    }

    fun getTreat(): LiveData<TreatResponse> {
        return treat
    }

}