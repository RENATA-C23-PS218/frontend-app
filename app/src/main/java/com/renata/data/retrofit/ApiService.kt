package com.renata.data.retrofit

import com.renata.data.user.register.RegisterResponse
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
    suspend fun login()

    @FormUrlEncoded
    @POST("forgot-password")
    suspend fun forgotPass()

    @FormUrlEncoded
    @POST("verify-reset-password")
    suspend fun verifResetPass()

    @FormUrlEncoded
    @PUT("reset-password")
    suspend fun resetPass()

    @FormUrlEncoded
    @GET("profile")
    suspend fun getProfile()

    @FormUrlEncoded
    @POST("profile")
    suspend fun updateProfile()

    @FormUrlEncoded
    @POST("profile-image")
    suspend fun uploadProfilePict()

}