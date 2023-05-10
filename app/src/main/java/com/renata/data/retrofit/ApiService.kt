package com.renata.data.retrofit

import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register()

    @FormUrlEncoded
    @POST("login")
    suspend fun login()

    @FormUrlEncoded
    @POST("profile")
    suspend fun profile()

    @FormUrlEncoded
    @POST("profile")
    suspend fun updateProfile()
}