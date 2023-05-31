package com.renata.data

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.renata.data.retrofit.ApiConfig
import com.renata.data.retrofit.ApiService
import com.renata.data.user.forgotpass.ForgotPassResponse
import com.renata.data.user.login.LoginResponse
import com.renata.data.user.register.RegisterResponse
import com.renata.data.user.resetpass.ResetPassResponse
import com.renata.data.user.verifyemail.VerifyEmailResponse
import com.renata.ml.Model
import org.json.JSONObject
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.HttpException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class RenataRepository(private val application: Application) {

    private var imageSize: Int = 224
    private val apiService: ApiService = ApiConfig.getApiService()
    private val TAG = "RenataRepository"

    fun classifyImage(image: Bitmap): String {
        try {
            val model: Model = Model.newInstance(application.applicationContext)
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )
            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 1))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)
            val outputs: Model.Outputs = model.process(inputFeature0)
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
            val confidences: FloatArray = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val classes = arrayOf(
                "Unknown",
                "Aluvial",
                "Andosol",
                "Entisol",
                "Humus",
                "Inceptisol",
                "Kapur",
                "Laterit",
                "Pasir"
            )
            val detectedClass = classes[maxPos]
            model.close()

            Log.d(TAG, "Image classification result: $detectedClass")
            return detectedClass
        } catch (e: IOException) {
            throw Exception("Failed to classify image")
        }
    }

    fun register(
        email: String,
        password: String,
        confirmPass: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(
                email,
                password,
                confirmPass
            )
            if (response.success) {
                Log.d(TAG, "Registration success: ${response.message}")
                emit(Result.Success(response))
            } else {
                val errorResponse = JSONObject(response.message)
                val errorMessage = errorResponse.getString("message")
                Log.d(TAG, "Registration error: $errorMessage")
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val httpCode = e.code()
                    when (httpCode) {
                        400 -> "The email is already in use"
                        401 -> "Access unauthorized. Please provide valid credentials"
                        403 -> "Access forbidden. You don't have permission to perform this action"
                        404 -> "The requested resource was not found"
                        500 -> "Internal server error. Please try again later"
                        else -> "An HTTP error occurred with code $httpCode"
                    }
                }
                else -> "Registration exception: ${e.message}"
            }
            Log.e(TAG, errorMessage)
            emit(Result.Error(errorMessage))
        }
    }


    fun authentication(
        id: String,
        otp: String
    ): LiveData<Result<VerifyEmailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.verifyEmail(
                id,
                otp
            )
            if (response.success) {
                Log.d(TAG, "Authentication success: ${response.message}")
                emit(Result.Success(response))
            } else {
                Log.d(TAG, "Authentication error: ${response.message}")
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val httpCode = e.code()
                    when (httpCode) {
                        400 -> "Incorrect One-Time Password (OTP)"
                        401 -> "Access unauthorized. Please provide valid credentials"
                        403 -> "Access forbidden. You don't have permission to perform this action"
                        404 -> "User not found"
                        500 -> "Internal server error. Please try again later"
                        else -> "An HTTP error occurred with code $httpCode"
                    }
                }
                else -> "Authentication exception: ${e.message}"
            }
            Log.e(TAG, errorMessage)
            emit(Result.Error(errorMessage))
        }
    }

    fun login(
        email: String,
        password: String
    ): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.success) {
                Log.d(TAG, "Login success: ${response.message}")
                emit(Result.Success(response))
            } else {
                Log.d(TAG, "Login error: ${response.message}")
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is HttpException -> {
                    val httpCode = e.code()
                    when (httpCode) {
                        400 -> "Email verification required"
                        401 -> "Access unauthorized. Please provide valid credentials"
                        403 -> "Access forbidden. You don't have permission to perform this action"
                        404 -> "The account information does not match our records"
                        500 -> "Internal server error. Please try again later"
                        else -> "An HTTP error occurred with code $httpCode"
                    }
                }
                else -> "Login exception: ${e.message}"
            }
            Log.e(TAG, errorMessage)
            emit(Result.Error(errorMessage))
        }
    }

    fun userForgotPass(
        email: String,
    ): LiveData<Result<ForgotPassResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.forgotPass(
                email
            )
            if (response.success) {
                Log.d(TAG, "Forgot password success: ${response.message}")
                emit(Result.Success(response))
            } else {
                Log.d(TAG, "Forgot password error: ${response.message}")
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Forgot password exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun userResetPass(
        email: String,
        password: String,
        confirmPass: String
    ): LiveData<Result<ResetPassResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.resetPass(
                email,
                password,
                confirmPass
            )
            if (response.success) {
                Log.d(TAG, "Reset password success: ${response.message}")
                emit(Result.Success(response))
            } else {
                Log.d(TAG, "Reset password error: ${response.message}")
                emit(Result.Error(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Reset password exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }
}