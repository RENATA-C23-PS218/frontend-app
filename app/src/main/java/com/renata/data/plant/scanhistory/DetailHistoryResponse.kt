package com.renata.data.plant.scanhistory

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class DetailHistoryResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val dataDetailHistory: DataDetailHistory
) : Parcelable

@Parcelize
data class DataDetailHistory(
    @SerializedName("soil_type")
    val soil_Type: String,

    @SerializedName("date_scan")
    val date: String,

    @SerializedName("plant_recommendations")
    val plant: List<String>,

    @SerializedName("soil_image")
    val image: String
) : Parcelable