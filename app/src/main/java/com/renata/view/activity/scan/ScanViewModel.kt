package com.renata.view.activity.scan

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renata.data.RenataRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ScanViewModel(application: Application) : ViewModel() {
    private val renataRepository: RenataRepository = RenataRepository(application)
    fun classifyImage(image: Bitmap): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            try {
                val detectedClass = renataRepository.classifyImage(image)
                result.postValue(detectedClass)
            } catch (e: Exception) {
                result.postValue("")
            }
        }
        return result
    }

    fun cropRecommendation(token: String, soilType: RequestBody, image: MultipartBody.Part) =
        renataRepository.cropRecomm(token, soilType, image)
}