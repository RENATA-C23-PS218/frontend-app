package com.renata.data.retrofit

import com.renata.data.user.forgotpass.ForgotPassResponse
import com.renata.data.user.login.LoginResponse
import com.renata.data.user.register.RegisterResponse
import com.renata.data.user.resetpass.ResetPassResponse
import com.renata.data.user.updateprofile.*
import com.renata.data.user.verifyresetpass.VerifyResetPassResponse
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

    @FormUrlEncoded
    @POST("verify")
    suspend fun verifyEmail()

    @FormUrlEncoded
    @POST("resend-verification")
    suspend fun resendVerif()

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

    @FormUrlEncoded
    @POST("verify-reset-password")
    suspend fun verifResetPass(
        @Field("email") email: String,
        @Field("otp") otp: Int
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
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("phone") phone: String,
        @Field("address") address: String
    ): Call<UpdateProfileResponse>

    @FormUrlEncoded
    @POST("profile-image")
    suspend fun uploadProfilePict()

}