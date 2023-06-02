package com.renata.data.retrofit

import com.renata.data.user.forgotpass.ForgotPassResponse
import com.renata.data.user.login.LoginResponse
import com.renata.data.user.register.RegisterResponse
import com.renata.data.user.resetpass.ResetPassResponse
import com.renata.data.user.updateprofile.*
import com.renata.data.user.verifyemail.ResendOTPResponse
import com.renata.data.user.verifyemail.VerifyEmailRequest
import com.renata.data.user.verifyemail.VerifyEmailResponse
import com.renata.data.user.verifyresetpass.VerifyResetPassRequest
import com.renata.data.user.verifyresetpass.VerifyResetPassResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String
    ): RegisterResponse

    @POST("verify")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun verifyEmail(
        @Body requestBody: VerifyEmailRequest
    ): VerifyEmailResponse


    @FormUrlEncoded
    @POST("resend-verification")
    suspend fun resendVerif(
        @Field("id") id: String
    ): ResendOTPResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("forgot-password")
    suspend fun forgotPass(
        @Field("email") email: String
    ): ForgotPassResponse

    @POST("verify-reset-password")
    @Headers("Content-Type: application/json; charset=UTF-8")
    suspend fun verifResetPass(
        @Body requestBody: VerifyResetPassRequest
    ): VerifyResetPassResponse

    @FormUrlEncoded
    @PUT("reset-password")
    suspend fun resetPass(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String
    ): ResetPassResponse

    @FormUrlEncoded
    @GET("profile")
    suspend fun getProfile()

    @FormUrlEncoded
    @POST("profile")
    fun updateProfile(
        @Header("Authorization") Bearer: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("phone") phone: String,
        @Field("address") address: String
    ): Call<UpdateProfileResponse>

    @Multipart
    @POST("profile-image")
    fun uploadProfilePict(
        @Header("Authorization") Bearer: String,
        @Part file: MultipartBody.Part
    ): Call<UpdatePhotoResponse>

}