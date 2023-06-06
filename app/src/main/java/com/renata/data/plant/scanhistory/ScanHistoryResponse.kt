package com.renata.data.plant.scanhistory

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ScanHistoryResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val dataHistory: DataHistory
) : Parcelable

@Parcelize
data class DataHistory(
    @SerializedName("user_id")
    val id: String,

    @SerializedName("scanHistories")
    val scanHistory: List<ScanHistory>
) : Parcelable

@Parcelize
data class ScanHistory(
    @SerializedName("soil_type")
    val soil_Type: String,

    @SerializedName("date_scan")
    val date: String,

    @SerializedName("soil_image")
    val image: String
) : Parcelable