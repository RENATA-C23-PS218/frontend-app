package com.renata.data.plant.scanhistory

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ScanHistoryResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val dataHistory: DataHistory
) : Parcelable

@Parcelize
data class DataHistory(
    @field:SerializedName("user_id")
    val id: String,

    @field:SerializedName("scan_Histories")
    val scanHistory: List<ScanHistory>,
) : Parcelable

@Parcelize
data class ScanHistory(
    @field:SerializedName("soil_type")
    val soil_Type: String,

    @field:SerializedName("date_scan")
    val date: List<ScanHistory>,
) : Parcelable